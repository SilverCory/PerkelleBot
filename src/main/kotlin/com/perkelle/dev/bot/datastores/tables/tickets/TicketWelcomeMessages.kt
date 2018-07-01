package com.perkelle.dev.bot.datastores.tables.tickets

import com.perkelle.dev.bot.datastores.DataStore
import com.perkelle.dev.bot.datastores.upsert
import com.perkelle.dev.bot.getConfig
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

object TicketWelcomeMessages: DataStore {

    private object Store: Table(getConfig().getTablePrefix() + "ticketwelcome") {
        val guild = long("guild").uniqueIndex()
        val message = text("message")
    }

    override val instance: Table
        get() = Store

    override fun getTable() = instance

    private val cache = mutableMapOf<Long, String?>()

    fun getMessage(guild: Long): String {
        if(!cache.containsKey(guild)) populateCache(guild)

        return cache[guild] ?: "Thanks for reaching out to us. A support representative will be with you shortly."
    }

    fun setMessage(guild: Long, message: String) {
        cache[guild] = message

        transaction {
            Store.upsert(listOf(Store.message)) {
                it[Store.guild] = guild
                it[Store.message] = message
            }
        }
    }

    private fun populateCache(guild: Long) {
        val message = transaction {
            Store.select {
                Store.guild eq guild
            }.map { it[Store.message] }.firstOrNull()
        }

        cache[guild] = message
    }
}