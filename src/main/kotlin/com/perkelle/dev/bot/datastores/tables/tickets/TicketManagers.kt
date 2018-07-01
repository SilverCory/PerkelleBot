package com.perkelle.dev.bot.datastores.tables.tickets

import com.perkelle.dev.bot.datastores.DataStore
import com.perkelle.dev.bot.utils.with
import com.perkelle.dev.bot.utils.without
import net.dv8tion.jda.core.entities.Member
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object TicketManagers: DataStore {

    private object Store: Table("ticketmanagers") {
        val guild = long("guild")
        val manager = long("manager")
    }

    override val instance: Table
        get() = Store

    override fun getTable() = instance

    private val cache = mutableMapOf<Long, List<Long>>()

    fun isManager(member: Member): Boolean {
        if(!cache.containsKey(member.guild.idLong)) populateCache(member.guild.idLong)

        return cache[member.guild.idLong]!!.contains(member.user.idLong)
    }

    fun getManagers(guild: Long): List<Long> {
        if(!cache.containsKey(guild)) populateCache(guild)

        return cache[guild] ?: listOf()
    }

    fun addManager(guild: Long, manager: Long) {
        if(!cache.containsKey(guild)) populateCache(guild)

        cache[guild] = cache[guild]!!.with(manager)

        transaction {
            Store.insert {
                it[Store.guild] = guild
                it[Store.manager] = manager
            }
        }
    }

    fun removeManager(guild: Long, manager: Long) {
        if(!cache.containsKey(guild)) populateCache(guild)

        cache[guild] = cache[guild]!!.without(manager)

        transaction {
            Store.deleteWhere {
                (Store.guild eq guild) and (Store.manager eq manager)
            }
        }
    }

    private fun populateCache(guild: Long) {
        val managers = transaction {
            Store.select {
                Store.guild eq guild
            }.map { it[Store.manager] }
        }

        cache[guild] = managers
    }
}