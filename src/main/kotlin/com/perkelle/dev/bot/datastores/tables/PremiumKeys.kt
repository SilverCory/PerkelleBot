package com.perkelle.dev.bot.datastores.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

object PremiumKeys {

    private object Store: Table("premiumkeys") {
        val key = varchar("key", 36)
        val months = integer("months")
    }

    fun isValid(key: String): Boolean {
        return transaction {
            Store.select {
                Store.key eq key
            }.firstOrNull() != null
        }
    }

    fun getMonths(key: String): Int {
        return transaction {
            Store.select {
                Store.key eq key
            }.map { it[Store.months] }.firstOrNull() ?: 0
        }
    }

    fun addKey(key: String, months: Int) {
        transaction {
            Store.insert {
                it[Store.key] = key
                it[Store.months] = months
            }
        }
    }

    fun removeKey(key: String) {
        transaction {
            Store.deleteWhere {
                Store.key eq key
            }
        }
    }
}