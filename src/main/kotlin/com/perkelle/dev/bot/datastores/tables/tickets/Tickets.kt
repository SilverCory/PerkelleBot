package com.perkelle.dev.bot.datastores.tables.tickets

import com.perkelle.dev.bot.command.impl.tickets.Ticket
import com.perkelle.dev.bot.datastores.DataStore
import com.perkelle.dev.bot.getConfig
import net.dv8tion.jda.core.entities.Channel
import net.dv8tion.jda.core.entities.Member
import org.jetbrains.exposed.sql.AutoIncColumnType
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

object Tickets : DataStore {

    private object Store : Table(getConfig().getTablePrefix() + "") {
        val guild = long("guild")
        val channel = long("channel")
        val id = integer("id").autoIncrement()
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

        (Store.id as AutoIncColumnType).autoincSeq
        return cache.firstOrNull { it.channel == channel }
    }

    fun createTicket(channel: Channel, owner: Member): Ticket {
        val id = transaction {
            Store.insert {
                it[Store.guild] = channel.guild.idLong
                it[Store.channel] = channel.idLong
                it[Store.open] = true
                it[Store.owner] = owner.user.idLong
            }.generatedKey
        }

        val ticket = Ticket(channel.guild.idLong, channel.idLong, id!!.toInt(), true, owner.user.idLong)
        cache.add(ticket)

        return ticket
    }

    private fun populateCache(id: Int) {
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