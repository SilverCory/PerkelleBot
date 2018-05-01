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

object BlacklistedMembers {

    private val cache = mutableMapOf<Long, Boolean>() //Member ID -> Blacklisted

    object Store: Table("${getConfig().getTablePrefix()}blacklist") {
        val member = long("id").uniqueIndex().primaryKey()
    }

    fun isBlacklisted(member: Long, callback: (Boolean) -> Unit) {
        if(cache.containsKey(member)) {
            callback(cache[member]!!)
        } else {
            async {
                transaction {
                    Store.select {
                        Store.member eq member
                    }.map { it[Store.member] }.firstOrNull() != null
                }
            }.onComplete {
                cache[member] = it
                callback(it)
            }
        }
    }

    fun addBlacklist(member: Long) {
        cache[member] = true
        launch {
            transaction {
                Store.insert {
                    it[Store.member] = member
                }
            }
        }
    }

    fun removeBlacklist(member: Long) {
        cache[member] = false
        launch {
            transaction {
                Store.deleteWhere {
                    Store.member eq member
                }
            }
        }
    }
}