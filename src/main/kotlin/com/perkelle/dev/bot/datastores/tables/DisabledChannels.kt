package com.perkelle.dev.bot.datastores.tables

import com.perkelle.dev.bot.getConfig
import com.perkelle.dev.bot.utils.onComplete
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

object DisabledChannels {

    object Store: Table("${getConfig().getTablePrefix()}disabledchannels") {
        val channel = long("channel").uniqueIndex().primaryKey()
    }

    fun isDisabled(channelId: Long, callback: (Boolean) -> Unit) {
        async {
            return@async transaction {
                Store.select {
                    Store.channel eq channelId
                }.count() == 1
            }
        }.onComplete(callback)
    }

    fun setDisabled(channelId: Long, isDisabled: Boolean) {
        launch {
            if(isDisabled) {
                transaction {
                    Store.insert {
                        it[channel] = channelId
                    }
                }
            } else {
                transaction {
                    Store.deleteWhere {
                        Store.channel eq channelId
                    }
                }
            }
        }
    }
}