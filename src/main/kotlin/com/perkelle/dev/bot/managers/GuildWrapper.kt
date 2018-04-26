package com.perkelle.dev.bot.managers

import com.perkelle.dev.bot.command.PermissionList
import com.perkelle.dev.bot.command.datastores.getSQLBackend
import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.entities.Role

class GuildWrapper(id: Long, shard: JDA) {

    init {
        val guild = shard.getGuildById(id)

        getSQLBackend().getPrefix(id) {
            prefix = it
        }

        getSQLBackend().getEveryonePermissions(id) {
            defaultPermissions = it
        }

        guild.roles.forEach { role ->
            getSQLBackend().getRolePermissions(role) {
                if(it != null) rolePermissions[role] = it
            }
        }

        guild.textChannels.map { it.idLong }.forEach { id ->
            getSQLBackend().isDisabled(id) {
                if(it) disabledChannels.add(id)
            }
        }
    }

    var prefix: String? = null
    lateinit var defaultPermissions: PermissionList
    val rolePermissions = mutableMapOf<Role, PermissionList>()
    val disabledChannels = mutableListOf<Long>()
}