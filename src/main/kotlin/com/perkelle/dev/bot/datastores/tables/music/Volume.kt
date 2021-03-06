package com.perkelle.dev.bot.datastores.tables.music

import com.perkelle.dev.bot.datastores.DataStore
import com.perkelle.dev.bot.datastores.upsert
import com.perkelle.dev.bot.getConfig
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

object Volume: DataStore {

    private object Store: Table("${getConfig().getTablePrefix()}volume") {
        val guild = long("guild").uniqueIndex().primaryKey()
        val volume = integer("volume")
    }

    override val instance: Table
        get() = Store

    override fun getTable() = instance

    fun setVolume(guildId: Long, volume: Int) {
        transaction {
            Store.upsert(listOf(Store.volume)) {
                it[guild] = guildId
                it[Store.volume] = volume
            }
        }
    }

    fun getVolume(guildId: Long): Int {
        return transaction {
            Store.select {
                Store.guild eq guildId
            }.map { it[Store.volume] }.firstOrNull() ?: 100
        }
    }
}