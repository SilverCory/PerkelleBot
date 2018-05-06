package com.perkelle.dev.bot.datastores.tables

import com.perkelle.dev.bot.datastores.DataStore
import com.perkelle.dev.bot.datastores.upsert
import com.perkelle.dev.bot.getConfig
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

object WelcomeMessages: DataStore {

    private object Store: Table("${getConfig().getTablePrefix()}welcomemessages") {
        val guild = long("guild").uniqueIndex().primaryKey()
        val message = text("message")
    }

    override val instance: Table
        get() = Store

    override fun getTable() = instance

    private val cache = mutableMapOf<Long, String?>()

    fun getWelcomeMessage(guild: Long): String? {
        if(cache.containsKey(guild)) return cache[guild]

        val message = transaction {
            Store.select {
                Store.guild eq guild
            }.map { it[Store.message] }.firstOrNull()
        }

        cache[guild] = message
        return message
    }

    fun setWelcomeMessage(guild: Long, message: String?) {
        cache[guild] = message

        if(message == null) {
            transaction {
                Store.deleteWhere {
                    Store.guild eq guild
                }
            }
        } else {
            transaction {
                Store.upsert(listOf(Store.message)) {
                    it[Store.guild] = guild
                    it[Store.message] = message
                }
            }
        }
    }
}