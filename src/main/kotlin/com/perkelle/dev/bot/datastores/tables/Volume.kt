package com.perkelle.dev.bot.datastores.tables

import com.perkelle.dev.bot.datastores.upsert
import com.perkelle.dev.bot.getConfig
import com.perkelle.dev.bot.utils.onComplete
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

object Volume {

    object Store: Table("${getConfig().getTablePrefix()}volume") {
        val guild = long("guild").uniqueIndex().primaryKey()
        val volume = integer("volume")
    }

    fun setVolume(guildId: Long, volume: Int) {
        launch {
            transaction {
                Store.upsert(listOf(Store.volume)) {
                    it[guild] = guildId
                    it[this.volume] = volume
                }
            }
        }
    }

    fun getVolume(guildId: Long, callback: (Int) -> Unit) {
        async {
            transaction {
                Store.select {
                    Store.guild eq guildId
                }.map { it[Store.volume] }.firstOrNull() ?: 100
            }
        }.onComplete(callback)
    }
}