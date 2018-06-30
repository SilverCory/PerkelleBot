package com.perkelle.dev.bot.datastores.tables.roles

import com.perkelle.dev.bot.datastores.DataStore
import com.perkelle.dev.bot.datastores.upsert
import com.perkelle.dev.bot.getConfig
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

object AutoRole: DataStore {

    private object Store: Table(getConfig().getTablePrefix() + "autorole") {
        val guild = long("guild").uniqueIndex()
        val role = long("role")
    }

    override val instance: Table
        get() = Store

    override fun getTable() = instance

    private val cache = mutableMapOf<Long, Long?>() // Guild ID -> Role ID

    fun hasAutoRole(guild: Long): Boolean {
        if(!cache.containsKey(guild)) populateCache(guild)

        return cache[guild] != null
    }

    fun getRole(guild: Long): Long? {
        if(!cache.containsKey(guild)) populateCache(guild)

        return cache[guild]
    }

    fun setRole(guild: Long, role: Long) {
        cache[guild] = role

        transaction {
            Store.upsert(listOf(Store.role)) {
                it[Store.guild] = guild
                it[Store.role] = role
            }
        }
    }

    fun disableAutoRoll(guild: Long) {
        cache[guild] = null

        transaction {
            Store.deleteWhere {
                Store.guild eq guild
            }
        }
    }

    private fun populateCache(guild: Long) {
        val role = transaction {
            Store.select {
                Store.guild eq guild
            }.map { it[Store.role] }.firstOrNull()
        }

        cache[guild] = role
    }
}