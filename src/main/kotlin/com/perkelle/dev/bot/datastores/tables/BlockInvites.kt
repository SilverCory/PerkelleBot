package com.perkelle.dev.bot.datastores.tables

import com.perkelle.dev.bot.datastores.DataStore
import com.perkelle.dev.bot.getConfig
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

object BlockInvites: DataStore {

    private object Store: Table("${getConfig().getTablePrefix()}blockinvites") {
        val guild = long("id").uniqueIndex().primaryKey()
    }

    override val instance: Table
        get() = Store

    override fun getTable() = instance

    private val cache = mutableMapOf<Long, Boolean>()

    fun toggleBlocked(guild: Long, blocked: Boolean) {
        if(blocked) {
            cache[guild] = true
            transaction {
                Store.insert {
                    it[Store.guild] = guild
                }
            }
        } else {
            cache[guild] = false
            transaction {
                Store.deleteWhere {
                    Store.guild eq guild
                }
            }
        }
    }

    fun isBlocked(guild: Long): Boolean {
        if(cache.containsKey(guild)) return cache[guild]!!

        val blocked = transaction {
            Store.select {
                Store.guild eq guild
            }.map { it[Store.guild] }.isNotEmpty()
        }

        cache[guild] = blocked
        return blocked
    }
}