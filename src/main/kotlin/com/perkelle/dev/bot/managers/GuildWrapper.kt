package com.perkelle.dev.bot.managers

import com.perkelle.dev.bot.PerkelleBot
import com.perkelle.dev.bot.command.PermissionList
import com.perkelle.dev.bot.datastores.tables.*
import com.perkelle.dev.bot.music.GuildMusicManager
import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.entities.Role

class GuildWrapper(val id: Long, val shard: JDA) {

    var prefix: String? = null
    lateinit var defaultPermissions: PermissionList
    val rolePermissions = mutableMapOf<Role, PermissionList>()
    val disabledChannels = mutableListOf<Long>()
    val musicManager = GuildMusicManager(PerkelleBot.instance.playerManager.createPlayer(), id, shard)
    var nowPlaying: Message? = null

    init {
        val guild = shard.getGuildById(id)

        Prefixes.getPrefix(id) {
            prefix = it
        }

        DefaultPermissions.getEveryonePermissions(id) {
            defaultPermissions = it
        }

        guild.roles.forEach { role ->
            RolePermissions.getRolePermissions(role) {
                if(it != null) rolePermissions[role] = it
            }
        }

        guild.textChannels.map { it.idLong }.forEach { channelId ->
            DisabledChannels.isDisabled(channelId) {
                if(it) disabledChannels.add(channelId)
            }
        }
    }

    private fun getGuild() = shard.getGuildById(id)

    fun isPremium(callback: (Boolean) -> Unit) = PremiumUsers.isPremium(getGuild().owner.user.idLong, callback)
}