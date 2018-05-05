package com.perkelle.dev.bot.datastores.tables

import com.perkelle.dev.bot.getConfig
import net.dv8tion.jda.core.entities.Member
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object BlacklistedMembers {

    private val cache = mutableMapOf<Pair<Long, Long>, Boolean>() //Guild ID + User ID -> Blacklisted

    private object Store: Table("${getConfig().getTablePrefix()}blacklist") {
        val guild = long("guild")
        val member = long("id")
    }

    fun isBlacklisted(member: Member): Boolean {
        val cached = cache.entries.firstOrNull { it.key.first == member.guild.idLong && it.key.second == member.user.idLong }?.value

        return if(cached != null) cached
        else {
            val blacklisted = transaction {
                Store.select {
                    Store.member eq member.user.idLong and (Store.guild eq member.guild.idLong)
                }.map { it[Store.guild] to it[Store.member] }.firstOrNull() != null
            }

            cache[member.guild.idLong to member.user.idLong] = blacklisted
            return blacklisted
        }
    }

    fun addBlacklist(guild: Long, user: Long) {
        cache[guild to user] = true
        transaction {
            Store.insert {
                it[Store.guild] = guild
                it[Store.member] = user
            }
        }
    }

    fun removeBlacklist(guild: Long, user: Long) {
        cache[guild to user] = false
        transaction {
            Store.deleteWhere {
                Store.guild eq guild and (Store.member eq user)
            }
        }
    }
}