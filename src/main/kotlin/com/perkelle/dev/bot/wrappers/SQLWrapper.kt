package com.perkelle.dev.bot.wrappers

import com.perkelle.dev.bot.utils.Callback
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.util.*

abstract class SQLWrapper(private val host: String, private val username: String, private val password: String, private val database: String, private val port: Int = 3306) {

    companion object {
        var threadPoolSize = 10

        var connected = false
        lateinit var ds: HikariDataSource
    }

    val ALL = "*"

    fun login() : Boolean {
        return try {
            if(connected) return true

            val timezone = TimeZone.getDefault().id

            val config = HikariConfig()
            config.jdbcUrl = "jdbc:mysql://$host:$port/$database"
            config.username = username
            config.password = password
            config.addDataSourceProperty("autoReconnect", true)
            config.addDataSourceProperty("useJDBCCompliantTimezoneShift", true)
            config.addDataSourceProperty("useLegacyDatetimeCode", false)
            config.addDataSourceProperty("serverTimezone", timezone)
            config.addDataSourceProperty("cachePrepStmts", true)
            config.addDataSourceProperty("prepStmtCacheSize", 250)
            config.addDataSourceProperty("prepStmtCacheSqlLimit", 2048)
            config.addDataSourceProperty("maxIdle", 1)
            config.maximumPoolSize = threadPoolSize

            ds = HikariDataSource(config)
            connected = true

            true
        } catch(ex: SQLException) {
            false
        }
    }

    abstract fun setup()

    fun closeConnection() = ds.close()

    /**
     * @param name The name of the table
     * @param keys A list of SQLKeys
     */
    fun createTable(name: String, keys: List<SQLKey>) {
        var query = "CREATE TABLE IF NOT EXISTS $name ("
        for((name1, dataType, length, unique, autoIncrement, primaryKey) in keys) {
            query+= "$name1 "
            query+= dataType.name
            if(length != null) query+="($length)"
            if(autoIncrement) query+= " AUTO INCREMENT "
            if(primaryKey) query+= " PRIMARY KEY "
            if(unique) query+= " UNIQUE "
            query+=","
        }
        query = query.substring(0, query.length-1)
        query+=");"

        execute(query)
    }

    fun selectData(table: String, f: List<String>, condition: ConditionBuilder? = null, callback: Callback<List<SQLRow>>) {
        launch {
            val data = selectDataSync(table, f, condition)
            runBlocking {
                callback(data)
            }
        }
    }

    fun selectDataSync(table: String, f: List<String>, condition: ConditionBuilder? = null): List<SQLRow> {
        var query = "SELECT "
        val fields = f.toMutableList()

        if(f[0] == "*") {
            fields.removeAt(0)
            getColumnsSync(table).forEach {
                query += "$it, "
                fields.add(it)
            }
        } else {
            f.forEach {
                query += "$it, "
            }
        }

        query = query.substring(0, query.length - 2)

        query+= " FROM $table"

        if(condition != null) query+= " WHERE "+condition.condition
        query+= ";"

        val triple = executeQuerySync(query)
        val data = mutableListOf<SQLRow>()
        val rs = triple.first

        while(rs.next()) {
            val row = mutableMapOf<String, String>()
            fields.forEach { row[it] = rs.getString(it) }
            data.add(SQLRow(row))
        }
        rs.close()
        triple.second.close()
        triple.third.close()

        return data
    }

    fun count(table: String, condition: ConditionBuilder? = null, callback: Callback<Int>) {
        launch {
            val result = countSync(table, condition)
            runBlocking {
                callback(result)
            }
        }
    }

    fun countSync(table: String, condition: ConditionBuilder? = null): Int {
        val firstColumn = getColumnsSync(table)[0]

        var query = "SELECT COUNT('$firstColumn') FROM $table"
        if(condition != null) query+= " WHERE "+condition.condition
        query+= ";"

        val triple = executeQuerySync(query)
        val rs = triple.first
        val count =
                if (rs.next()) rs.getInt(1)
                else 0

        rs.close()

        triple.second.close()
        triple.third.close()

        return count
    }

    private fun getColumnsSync(table: String): List<String> {
        val query = "SHOW COLUMNS FROM $table;"

        val triple = executeQuerySync(query)
        val rs = triple.first
        val columns = mutableListOf<String>()
        while(rs.next()) columns.add(rs.getString(1))
        rs.close()
        triple.second.close()
        triple.third.close()
        return columns
    }

    fun getColumns(table: String, callback: Callback<List<String>>) {
        launch {
            val columns = getColumnsSync(table)
            runBlocking {
                callback(columns)
            }
        }
    }

    /**
     * @param table The table name, duh
     * @param fields Field name, value
     * @param upserts If a unique key is already present, should we update it? Null if you don't want this to happen or there are no unique keys. The list should contain the unique keys.
     */
    fun insert(table: String, fields: Map<String, Any>, upserts: List<String>? = null) {
        var statement = "INSERT INTO $table("
        for(field in fields.keys) statement+="`$field`,"
        statement = statement.substring(0, statement.length-1)
        statement+=") VALUES("
        for(value in fields.values) {
            statement += if(value is Int || value is Long || value is Double || value is Float) "$value,"
            else "'$value',"
        }
        statement = statement.substring(0, statement.length-1)
        statement+= ")"
        if(upserts != null) {
            statement+= " ON DUPLICATE KEY UPDATE "
            for(unique in upserts) {
                val value = fields[unique]
                statement+= "`$unique` = "
                statement += if(value is Number) "$value,"
                else "'$value',"
            }
            statement = statement.substring(0, statement.length-1)
        }

        statement+= ";"
        execute(statement)
    }

    fun remove(table: String, condition: ConditionBuilder? = null) {
        var statement = "DELETE FROM $table"
        if(condition != null) statement+= " WHERE "+condition.condition
        statement+= ";"
        execute(statement)
    }

    fun update(table: String, fieldToUpdate: String, newValue: Any, condition: ConditionBuilder? = null) {
        var statement = "UPDATE $table SET $fieldToUpdate="

        if(newValue !is Number) statement += "'"
        statement += newValue.toString()
        if(newValue !is Number) statement += "'"

        statement += " ${condition?.condition};"

        execute(statement)
    }

    /**
     * @param name The name of the key
     * @param dataType @see SQLDataTypes
     * @param length The max length of the data, e.g. VARCHAR(36). Null for thing like TEXT where it doesn't exist
     * @param unique Is it a unique key
     * @param autoIncrement Is it an auto incremental key (ints only)
     */
    data class SQLKey(val name: String, val dataType: SQLDataTypes, val length: Int?, val unique: Boolean, val autoIncrement: Boolean = false, val primaryKey: Boolean = false)

    /**
     * @param The key. If you do SELECT * FROM myTable WHERE `NAME` = 'RYAN'; the key will be RYAN
     * @param values Column, Value
     */
    data class SQLRow(val fields: Map<String, String>)

    /**
     * All SQL datatypes
     */
    enum class SQLDataTypes {

        CHAR,
        VARCHAR,
        TINYTEXT,
        TEXT,
        MEDIUMTEXT,
        LONGTEXT,
        TINYINT,
        SMALLINT,
        MEDIUMINT,
        INT,
        BIGINT,
        FLOAT,
        //DOUBLE - Not Supported
        //DECIMAL - Not Supported
        DATE,
        DATETIME,
        TIMESTAMP,
        TIME,
        ENUM,
        SET,
        BINARY
    }

    class ConditionBuilder {

        var condition = ""

        fun or(): ConditionBuilder {
            condition+=" OR "
            return this
        }

        fun and(): ConditionBuilder {
            condition+= " AND "
            return this
        }

        fun equalTo(comparison: Pair<String, Any>): ConditionBuilder {
            condition += if(comparison.second is Number) " `${comparison.first}` = ${comparison.second} "
            else " `${comparison.first}` = '${comparison.second}' "
            return this
        }

        fun greaterThan(comparison: Pair<String, Any>): ConditionBuilder {
            condition+= " `${comparison.first}` > ${comparison.second} "
            return this
        }

        fun lessThan(comparison: Pair<String, Any>): ConditionBuilder {
            condition+= " `${comparison.first}` < ${comparison.second} "
            return this
        }
    }

    private fun execute(toExecute: String) {
        launch {
            val conn = ds.connection
            val ps = conn.prepareStatement(toExecute)
            ps.execute()
            ps.close()
            conn.close()
        }
    }

    private fun executeQuery(query: String, callback: Callback<Triple<ResultSet, PreparedStatement, Connection>>) {
        launch {
            val result = executeQuerySync(query)
            runBlocking {
                callback(result)
            }
        }
    }

    private fun executeQuerySync(query: String): Triple<ResultSet, PreparedStatement, Connection> {
        val conn = ds.connection
        val ps = conn.prepareStatement(query)
        val rs = ps.executeQuery()
        return Triple(rs, ps, conn)
    }
}
