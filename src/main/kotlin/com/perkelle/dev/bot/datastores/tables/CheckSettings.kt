package com.perkelle.dev.bot.datastores.tables

import com.perkelle.dev.bot.datastores.DataStore
import com.perkelle.dev.bot.getConfig
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

object CheckSettings: DataStore {

    private val delimiter = ','

    private object Store: Table("${getConfig().getTablePrefix()}checks") {
        val guild = long("guild").uniqueIndex().primaryKey()
        val blockInvites = bool("blockinvites")
        val blacklistedDomains = text("blacklistedDomains")
    }

    override fun getTable() = BlacklistedMembers.Store

    private fun getBlacklistedList(guild: Long): String {
        return transaction {
            Store.select {
                Store.guild eq guild
            }.map { it[Store.blacklistedDomains] }.firstOrNull() ?: ""
        }
    }

    fun isBlacklisted(guild: Long, domain: String) = getBlacklistedList(guild).toLowerCase().split(",").contains(domain.toLowerCase())
}