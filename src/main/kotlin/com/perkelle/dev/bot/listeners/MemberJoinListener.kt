package com.perkelle.dev.bot.listeners

import com.perkelle.dev.bot.datastores.tables.WelcomeMessages
import kotlinx.coroutines.experimental.launch
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter

class MemberJoinListener: ListenerAdapter() {

    override fun onGuildMemberJoin(e: GuildMemberJoinEvent) {
        val guild = e.guild
        val member = e.member
        val user = e.user

        launch {
            val welcomeMessage = WelcomeMessages.getWelcomeMessage(guild.idLong) ?: return@launch
            user.openPrivateChannel().queue { it.sendMessage(welcomeMessage).queue() }
        }
    }
}