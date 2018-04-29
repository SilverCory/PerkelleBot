package com.perkelle.dev.bot.listeners

import com.perkelle.dev.bot.datastores.tables.DefaultPermissions
import net.dv8tion.jda.core.events.guild.GuildJoinEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter

class JoinListener: ListenerAdapter() {

    override fun onGuildJoin(e: GuildJoinEvent) {
        val guild = e.guild
        DefaultPermissions.hasEveryonePermissions(guild) {
            if(!it) {
                DefaultPermissions.setDefaultEveryonePermissions(guild)
            }
        }
    }
}