package com.perkelle.dev.bot.datastores.tables.starboard

import com.perkelle.dev.bot.datastores.DataStore
import com.perkelle.dev.bot.datastores.upsert
import com.perkelle.dev.bot.getConfig
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

object StarboardChannels: DataStore {

    private object Store: Table(getConfig().getTablePrefix() + "starboardchannels") {
        val guild = long("guild").uniqueIndex()
        val channel = long("channel").uniqueIndex()
    }

    override val instance: Table
        get() = Store

    override fun getTable() = instance

    private val cache = mutableMapOf<Long, Long?>() // Guild -> Starboard Channel

    fun disableStarboard(guild: Long) {
        cache[guild] = null

        transaction {
            Store.deleteWhere {
                Store.guild eq guild
            }
        }
    }

    fun getStarboardChannel(guild: Long): Long? {
        if(!cache.containsKey(guild)) populateCache(guild)

        return cache[guild]
    }

    fun setStarboardChannel(guild: Long, channel: Long) {
        cache[guild] = channel

        transaction {
            Store.upsert(listOf(Store.channel)) {
                it[Store.guild] = guild
                it[Store.channel] = channel
            }
        }
    }

    private fun populateCache(guild: Long) {
        val channel = transaction {
            Store.select {
                Store.guild eq guild
            }.map { it[Store.channel] }.firstOrNull()
        }

        cache[guild] = channel
    }
}