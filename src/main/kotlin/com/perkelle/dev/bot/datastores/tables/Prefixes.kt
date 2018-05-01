package com.perkelle.dev.bot.datastores.tables

import com.perkelle.dev.bot.datastores.upsert
import com.perkelle.dev.bot.getConfig
import com.perkelle.dev.bot.utils.onCompleteOrNull
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

object Prefixes {

    object Store: Table("${getConfig().getTablePrefix()}prefixes") {
        val guild = long("guild").uniqueIndex().primaryKey()
        val prefix = varchar("prefix", 4)
    }

    fun setPrefix(guildId: Long, newPrefix: String) {
        launch {
            transaction {
                Store.upsert(listOf(Store.prefix)) {
                    it[guild] = guildId
                    it[prefix] = newPrefix
                }
            }
        }
    }

    fun getPrefix(guildId: Long, callback: (String?) -> Unit) {
        async {
            transaction {
                Store.select {
                    Store.guild eq guildId
                }.map { it[Store.prefix] }.firstOrNull()
            }
        }.onCompleteOrNull(callback)
    }
}