package com.perkelle.dev.bot.datastores

import com.perkelle.dev.bot.datastores.tables.*
import com.perkelle.dev.bot.getConfig
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.experimental.launch
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import javax.sql.DataSource

fun getSQLBackend() = SQLBackend.instance

class SQLBackend {

    companion object {
        lateinit var instance: SQLBackend
    }

    init {
        instance = this
    }

    private lateinit var ds: DataSource

    fun setup() {
        val config = HikariConfig()
        config.jdbcUrl = "jdbc:mysql://${getConfig().getValue("mysql.host", "localhost")}:3306/${getConfig().getValue("mysql.database", "perkellebot")}"
        config.username = getConfig().getValue("mysql.username", "root")
        config.password = getConfig().getValue("mysql.password", "")
        config.addDataSourceProperty("autoReconnect", true)
        config.addDataSourceProperty("useJDBCCompliantTimezoneShift", true)
        config.addDataSourceProperty("serverTimezone", "UTC")
        config.addDataSourceProperty("useLegacyDatetimeCode", false)
        config.addDataSourceProperty("cachePrepStmts", true)
        config.addDataSourceProperty("prepStmtCacheSize", 250)
        config.addDataSourceProperty("prepStmtCacheSqlLimit", 2048)
        config.addDataSourceProperty("maxIdle", 1)
        config.maximumPoolSize = 5

        ds = HikariDataSource(config)

        Database.connect(ds)

        launch {
            transaction {
                listOf(
                        BlacklistedMembers,
                        BlockInvites,
                        DefaultPermissions,
                        DisabledChannels,
                        ModLogChannels,
                        Prefixes,
                        PremiumKeys,
                        PremiumUsers,
                        RolePermissions,
                        Volume,
                        WelcomeMessages
                ).forEach { SchemaUtils.create(it.getTable()) }
            }
        }
    }
}

//Support upserts
fun <T:Table> T.upsert(uniqueColumns: List<Column<*>>, body: T.(UpsertStatement<Number>) -> Unit): UpsertStatement<Number> = UpsertStatement<Number>(this, uniqueColumns).apply {
    body(this)
    execute(TransactionManager.current())
}

class UpsertStatement<Key: Any>(table: Table, val onDupUpdate: List<Column<*>>): InsertStatement<Key>(table, false) {
    override fun prepareSQL(transaction: Transaction): String {
        val onUpdateSQL = if(onDupUpdate.isNotEmpty()) {
            " ON DUPLICATE KEY UPDATE " + onDupUpdate.joinToString { "${transaction.identity(it)}=VALUES(${transaction.identity(it)})" }
        } else ""
        return super.prepareSQL(transaction) + onUpdateSQL
    }
}