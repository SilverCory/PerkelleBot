package com.perkelle.dev.bot.datastores.tables.settings

import com.perkelle.dev.bot.datastores.DataStore
import com.perkelle.dev.bot.datastores.upsert
import com.perkelle.dev.bot.getConfig
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

object Prefixes: DataStore {

    private object Store: Table("${getConfig().getTablePrefix()}prefixes") {
        val guild = long("guild").uniqueIndex().primaryKey()
        val prefix = varchar("prefix", 4)
    }

    override val instance: Table
        get() = Store

    override fun getTable() = instance

    fun setPrefix(guildId: Long, newPrefix: String) {
        transaction {
            Store.upsert(listOf(Store.prefix)) {
                it[guild] = guildId
                it[prefix] = newPrefix
            }
        }
    }

    fun getPrefix(guildId: Long): String? {
        return transaction {
            Store.select {
                Store.guild eq guildId
            }.map { it[Store.prefix] }.firstOrNull()
        }
    }
}