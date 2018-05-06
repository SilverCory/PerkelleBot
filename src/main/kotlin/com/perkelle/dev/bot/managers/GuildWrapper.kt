package com.perkelle.dev.bot.managers

import com.perkelle.dev.bot.command.PermissionList
import com.perkelle.dev.bot.datastores.tables.DefaultPermissions
import com.perkelle.dev.bot.datastores.tables.Prefixes
import com.perkelle.dev.bot.datastores.tables.PremiumUsers
import com.perkelle.dev.bot.datastores.tables.RolePermissions
import com.perkelle.dev.bot.music.GuildAudioControllerFactory
import kotlinx.coroutines.experimental.launch
import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.entities.Member
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.entities.Role

class GuildWrapper(val id: Long, val shard: JDA, callback: (GuildWrapper) -> Unit = {}) {

    var prefix: String? = null
    lateinit var defaultPermissions: PermissionList
    val rolePermissions = mutableMapOf<Role, PermissionList>()
    val disabledChannels = mutableListOf<Long>()
    val musicManager = GuildAudioControllerFactory.createController(getGuild())
    var nowPlaying: Message? = null
    val admins = mutableListOf<Member>()

    init {
        admins.addAll(getGuild().members.filter { it.hasPermission(Permission.ADMINISTRATOR) })

        launch {
            prefix = Prefixes.getPrefix(id)
            defaultPermissions = DefaultPermissions.getEveryonePermissions(id)

            callback(this@GuildWrapper)
        }
    }

    private fun getGuild() = shard.getGuildById(id)

    fun isPremium() = admins.any { PremiumUsers.isPremium(it.user.idLong) }

    fun getRolePermissions(role: Role): PermissionList? {
        return if(rolePermissions.containsKey(role)) rolePermissions[role]
        else {
            val permissions = RolePermissions.getRolePermissions(role)

            if(permissions != null) rolePermissions[role] = permissions
            else rolePermissions[role] = defaultPermissions

            permissions
        }
    }
}