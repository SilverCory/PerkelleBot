package com.perkelle.dev.bot.datastores.tables.tickets

import com.perkelle.dev.bot.datastores.DataStore
import com.perkelle.dev.bot.datastores.upsert
import com.perkelle.dev.bot.getConfig
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

object TicketArchiveChannels : DataStore {

    private object Store : Table(getConfig().getTablePrefix() + "ticketarchives") {
        val guild = long("guild").uniqueIndex()
        val channel = long("channel")
    }

    override val instance: Table
        get() = Store

    override fun getTable() = instance

    private val cache = mutableMapOf<Long, Long?>()

    fun getChannel(guild: Long): Long? {
        if(!cache.containsKey(guild)) populateCache(guild)

        return cache[guild]
    }

    fun setChannel(guild: Long, channel: Long) {
        cache[guild] = channel

        transaction {
            Store.upsert(listOf(Store.channel)) {
                it[Store.guild] = guild
                it[Store.channel] = channel
            }
        }
    }

    fun disableArchiveChannel(guild: Long) {
        cache[guild] = null

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
            }.map { it[Store.channel] }.firstOrNull()
        }
    }
}