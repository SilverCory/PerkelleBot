package com.perkelle.dev.bot.datastores.tables.roles

import com.perkelle.dev.bot.datastores.DataStore
import com.perkelle.dev.bot.getBot
import com.perkelle.dev.bot.getConfig
import com.perkelle.dev.bot.utils.with
import com.perkelle.dev.bot.utils.without
import net.dv8tion.jda.core.entities.Role
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

object SelfAssignableRoles: DataStore {

    private object Store: Table("${getConfig().getTablePrefix()}selfassignableroles") {
        val role = long("role").uniqueIndex().primaryKey()
        val guild = long("guild")
    }

    override val instance: Table
        get() = Store

    override fun getTable() = instance

    private val selfAssignAllowedCache = mutableMapOf<Role, Boolean>()
    private val selfAssignListCache = mutableMapOf<Long, List<Role>>()

    fun isSelfAssignable(role: Long): Boolean {
        selfAssignAllowedCache.entries.firstOrNull { it.key.idLong == role }?.let { return it.value }

        val allowed = transaction {
            Store.select {
                Store.role eq role
            }.map { it[Store.role] }.count() > 0
        }

        selfAssignAllowedCache[getBot().shardManager.getRoleById(role)] = allowed

        return allowed
    }

    fun allowSelfAssign(role: Role, guild: Long) {
        selfAssignAllowedCache[role] = true
        selfAssignListCache[guild] = (selfAssignListCache[guild] ?: mutableListOf()).with(role)

        transaction {
            Store.insert {
                it[Store.role] = role.idLong
                it[Store.guild] = guild
            }
        }
    }

    fun disallowSelfAssign(role: Role, guild: Long) {
        selfAssignAllowedCache.remove(role)
        selfAssignListCache[guild] = (selfAssignListCache[guild] ?: mutableListOf()).without(role)

        transaction {
            Store.deleteWhere {
                Store.role eq role.idLong
            }
        }
    }

    fun getSelfAssignableRoles(guild: Long): List<Role> {
        selfAssignListCache[guild]?.let { return it }

        val roles = transaction {
            Store.select {
                Store.guild eq guild
            }.map { it[Store.role] }
        }.mapNotNull { getBot().shardManager.getRoleById(it) }

        selfAssignListCache[guild] = roles

        return roles
    }
}