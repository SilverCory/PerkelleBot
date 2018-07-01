package com.perkelle.dev.bot.datastores.tables.tickets

import com.perkelle.dev.bot.command.impl.tickets.Ticket
import com.perkelle.dev.bot.datastores.DataStore
import com.perkelle.dev.bot.getConfig
import com.perkelle.dev.bot.utils.generateRandomString
import net.dv8tion.jda.core.entities.Channel
import net.dv8tion.jda.core.entities.Member
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object Tickets : DataStore {

    private object Store : Table(getConfig().getTablePrefix() + "tickets") {
        val guild = long("guild")
        val channel = long("channel")
        val id = varchar("id", 8)
        val open = bool("open")
        val owner = long("owner")
    }

    override val instance: Table
        get() = Store

    override fun getTable() = instance

    private val cache = mutableListOf<Ticket>()

    fun isTicketChannel(channel: Long): Boolean {
        return transaction {
            Store.select {
                Store.channel eq channel
            }.count() > 0
        }
    }

    fun getTicket(channel: Long): Ticket? {
        if(cache.none { it.channel == channel }) populateCache(channel)

        return cache.firstOrNull { it.channel == channel }
    }

    fun createTicket(channel: Channel, id: String, owner: Member): Ticket {
        transaction {
            Store.insert {
                it[Store.guild] = channel.guild.idLong
                it[Store.channel] = channel.idLong
                it[Store.id] = id
                it[Store.open] = true
                it[Store.owner] = owner.user.idLong
            }
        }

        val ticket = Ticket(channel.guild.idLong, channel.idLong, id, true, owner.user.idLong)
        cache.add(ticket)

        return ticket
    }

    fun generateId(guild: Long): String {
        var found = false
        var id = ""

        while(!found) {
            id = generateRandomString(6)

            val alreadyExists = transaction {
                Store.select {
                    (Store.guild eq guild) and (Store.id eq id)
                }.count() > 0
            }

            found = !alreadyExists
        }

        return id
    }

    fun setClosed(channel: Long) {
        transaction {
            Store.update({ Store.channel eq channel }) {
                it[Store.open] = false
            }
        }
    }

    private fun populateCache(id: String) {
        val ticket = transaction {
            Store.select {
                Store.id eq id
            }.map { Ticket(it[Store.guild], it[Store.channel], it[Store.id], it[Store.open], it[Store.owner]) }.firstOrNull()
        } ?: return

        cache.add(ticket)
    }

    private fun populateCache(channel: Long) {
        val ticket = transaction {
            Store.select {
                Store.channel eq channel
            }.map { Ticket(it[Store.guild], it[Store.channel], it[Store.id], it[Store.open], it[Store.owner]) }.firstOrNull()
        } ?: return

        cache.add(ticket)
    }
}