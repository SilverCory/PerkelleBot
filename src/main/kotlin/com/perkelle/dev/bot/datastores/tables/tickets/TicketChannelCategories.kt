package com.perkelle.dev.bot.datastores.tables.tickets

import com.perkelle.dev.bot.datastores.DataStore
import com.perkelle.dev.bot.datastores.upsert
import com.perkelle.dev.bot.getConfig
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

object TicketChannelCategories : DataStore {

    private object Store : Table(getConfig().getTablePrefix() + "ticketcategories") {
        val guild = long("guild").uniqueIndex()
        val category = long("category")
    }

    override val instance: Table
        get() = Store

    override fun getTable() = instance

    private val cache = mutableMapOf<Long, Long?>()

    fun getCategory(guild: Long): Long? {
        if(!cache.containsKey(guild)) populateCache(guild)

        return cache[guild]
    }

    fun setCategory(guild: Long, category: Long) {
        cache[guild] = category

        transaction {
            Store.upsert(listOf(Store.category)) {
                it[Store.guild] = guild
                it[Store.category] = category
            }
        }
    }

    private fun populateCache(guild: Long) {
        cache[guild] = transaction {
            Store.select {
                Store.guild eq guild
            }.map { it[Store.category] }.firstOrNull()
        }
    }
}