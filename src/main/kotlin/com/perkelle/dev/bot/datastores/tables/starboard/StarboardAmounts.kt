package com.perkelle.dev.bot.datastores.tables.starboard

import com.perkelle.dev.bot.datastores.DataStore
import com.perkelle.dev.bot.datastores.upsert
import com.perkelle.dev.bot.getConfig
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

object StarboardAmounts: DataStore {

    private object Store: Table(getConfig().getTablePrefix() + "starboardstars") {
        val guild = long("guild").uniqueIndex()
        val amount = integer("amount")
    }

    override val instance: Table
        get() = Store

    override fun getTable() = instance

    private val cache = mutableMapOf<Long, Int>() // Guild -> Amount

    fun getAmount(guild: Long): Int {
        if(!cache.containsKey(guild)) populateCache(guild)

        return cache[guild]!!
    }

    fun setAmount(guild: Long, amount: Int) {
        cache[guild] = amount

        transaction {
            Store.upsert(listOf(Store.amount)) {
                it[Store.guild] = guild
                it[Store.amount] = amount
            }
        }
    }

    fun populateCache(guild: Long) {
        val amount = transaction {
            Store.select {
                Store.guild eq guild
            }.map { it[Store.amount] }
        }.firstOrNull() ?: 5

        cache[guild] = amount
    }
}