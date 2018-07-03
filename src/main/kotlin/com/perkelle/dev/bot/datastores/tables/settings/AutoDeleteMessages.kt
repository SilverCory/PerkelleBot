package com.perkelle.dev.bot.datastores.tables.settings

import com.perkelle.dev.bot.datastores.DataStore
import com.perkelle.dev.bot.getConfig
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

object AutoDeleteMessages : DataStore {

    private object Store : Table(getConfig().getTablePrefix() + "") {
        val guild = long("guild")
    }

    override val instance: Table
        get() = Store

    override fun getTable() = instance

    private val cache = mutableMapOf<Long, Boolean>()

    fun isAutoDelete(guild: Long): Boolean {
        if(!cache.containsKey(guild)) populateCache(guild)

        return cache[guild] ?: true
    }

    fun enableAutoDelete(guild: Long) {
        cache[guild] = true

        transaction {
            Store.insert {
                it[Store.guild] = guild
            }
        }
    }

    fun disableAutoDelete(guild: Long) {
        cache[guild] = false

        transaction {
            Store.deleteWhere {
                Store.guild eq guild
            }
        }
    }

    private fun populateCache(guild: Long) {
        cache[guild] = transaction {
            Store.select {
                Store.guild eq guild
            }.count() > 0
        }
    }
}