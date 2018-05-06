package com.perkelle.dev.bot.datastores.tables

import com.perkelle.dev.bot.datastores.DataStore
import com.perkelle.dev.bot.datastores.upsert
import com.perkelle.dev.bot.getConfig
import net.dv8tion.jda.core.entities.TextChannel
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

object ModLogChannels: DataStore {

    private object Store: Table("${getConfig().getTablePrefix()}modlog") {
        val guild = long("guild").uniqueIndex().primaryKey()
        val channel = long("channel")
    }

    override val instance: Table
        get() = Store

    override fun getTable() = instance

    private val cache = mutableMapOf<Long, Long?>()

    fun setChannel(channel: TextChannel) {
        val guild = channel.guild.idLong
        val id = channel.idLong

        transaction {
            Store.upsert(listOf(Store.channel)) {
                it[Store.guild] = guild
                it[Store.channel] = id
            }
        }
    }

    fun getChannel(guild: Long): Long? {
        if(cache.containsKey(guild)) return cache[guild]

        val id = transaction {
            Store.select {
                Store.guild eq guild
            }.map { it[Store.channel] }.firstOrNull()
        }

        cache[guild] = id
        return id
    }
}