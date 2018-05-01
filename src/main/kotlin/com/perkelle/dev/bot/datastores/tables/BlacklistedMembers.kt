package com.perkelle.dev.bot.datastores.tables

import com.perkelle.dev.bot.getConfig
import com.perkelle.dev.bot.utils.onComplete
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import net.dv8tion.jda.core.entities.Member
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object BlacklistedMembers {

    private val cache = mutableMapOf<Pair<Long, Long>, Boolean>() //Guild ID + User ID -> Blacklisted

    object Store: Table("${getConfig().getTablePrefix()}blacklist") {
        val guild = long("guild")
        val member = long("id")
    }

    fun isBlacklisted(member: Member, callback: (Boolean) -> Unit) {
        if(cache.any { it.key.first == member.guild.idLong && it.key.second == member.user.idLong }) {
            callback(cache.entries.first { it.key.first == member.guild.idLong && it.key.second == member.user.idLong }.value)
        } else {
            async {
                transaction {
                    Store.select {
                        Store.member eq member.user.idLong and (Store.guild eq member.guild.idLong)
                    }.firstOrNull() != null
                }
            }.onComplete {
                cache[member.guild.idLong to member.user.idLong] = it
                callback(it)
            }
        }
    }

    fun addBlacklist(guild: Long, user: Long) {
        cache[guild to user] = true
        launch {
            transaction {
                Store.insert {
                    it[Store.guild] = guild
                    it[Store.member] = user
                }
            }
        }
    }

    fun removeBlacklist(guild: Long, user: Long) {
        cache[guild to user] = false
        launch {
            transaction {
                Store.deleteWhere {
                    Store.guild eq guild and (Store.member eq user)
                }
            }
        }
    }
}