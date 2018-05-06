package com.perkelle.dev.bot.datastores.tables

import com.perkelle.dev.bot.datastores.DataStore
import com.perkelle.dev.bot.getConfig
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

object DisabledChannels: DataStore {

    private object Store: Table("${getConfig().getTablePrefix()}disabledchannels") {
        val channel = long("channel").uniqueIndex().primaryKey()
    }

    override val instance: Table
        get() = Store

    override fun getTable() = instance

    fun isDisabled(channelId: Long): Boolean {
        return transaction {
            Store.select {
                Store.channel eq channelId
            }.count() == 1
        }
    }

    fun setDisabled(channelId: Long, isDisabled: Boolean) {
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