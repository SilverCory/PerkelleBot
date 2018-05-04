package com.perkelle.dev.bot.managers

import com.perkelle.dev.bot.PerkelleBot
import com.perkelle.dev.bot.command.PermissionList
import com.perkelle.dev.bot.datastores.tables.DefaultPermissions
import com.perkelle.dev.bot.datastores.tables.Prefixes
import com.perkelle.dev.bot.datastores.tables.PremiumUsers
import com.perkelle.dev.bot.datastores.tables.RolePermissions
import com.perkelle.dev.bot.music.GuildMusicManager
import kotlinx.coroutines.experimental.launch
import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.entities.Role

class GuildWrapper(val id: Long, val shard: JDA, callback: (GuildWrapper) -> Unit = {}) {

    var prefix: String? = null
    lateinit var defaultPermissions: PermissionList
    val rolePermissions = mutableMapOf<Role, PermissionList>()
    val disabledChannels = mutableListOf<Long>()
    val musicManager = GuildMusicManager(PerkelleBot.instance.playerManager.createPlayer(), id, shard)
    var nowPlaying: Message? = null

    init {
        launch {
            prefix = Prefixes.getPrefix(id)
            defaultPermissions = DefaultPermissions.getEveryonePermissions(id)

            callback(this@GuildWrapper)
        }
    }

    private fun getGuild() = shard.getGuildById(id)

    fun isPremium() = PremiumUsers.isPremium(getGuild().owner.user.idLong)

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