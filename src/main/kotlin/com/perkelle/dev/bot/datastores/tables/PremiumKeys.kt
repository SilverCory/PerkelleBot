package com.perkelle.dev.bot.datastores.tables

import com.perkelle.dev.bot.utils.onComplete
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

object PremiumKeys {

    object Store: Table("premiumkeys") {
        val key = varchar("key", 36)
        val months = integer("months")
    }

    fun isValid(key: String, callback: (Boolean) -> Unit) {
        async {
            transaction {
                Store.select {
                    Store.key eq key
                }.firstOrNull() != null
            }
        }.onComplete(callback)
    }

    fun getMonths(key: String, callback: (Int) -> Unit) {
        async {
            transaction {
                Store.select {
                    Store.key eq key
                }.map { it[Store.months] }.firstOrNull() ?: 0
            }
        }.onComplete(callback)
    }

    fun addKey(key: String, months: Int) {
        launch {
            transaction {
                Store.insert {
                    it[Store.key] = key
                    it[Store.months] = months
                }
            }
        }
    }

    fun removeKey(key: String) {
        launch {
            transaction {
                Store.deleteWhere {
                    Store.key eq key
                }
            }
        }
    }
}