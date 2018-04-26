package com.perkelle.dev.bot.listeners

import com.perkelle.dev.bot.command.datastores.getSQLBackend
import net.dv8tion.jda.core.events.guild.GuildJoinEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter

class JoinListener: ListenerAdapter() {

    override fun onGuildJoin(e: GuildJoinEvent) {
        val guild = e.guild
        getSQLBackend().hasEveryonePermissions(guild) {
            if(!it) {
                getSQLBackend().setDefaultEveryonePermissions(guild)
            }
        }
    }
}