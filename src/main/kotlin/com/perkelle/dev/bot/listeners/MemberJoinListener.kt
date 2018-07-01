package com.perkelle.dev.bot.listeners

import com.perkelle.dev.bot.datastores.tables.roles.AutoRole
import com.perkelle.dev.bot.datastores.tables.settings.WelcomeMessages
import kotlinx.coroutines.experimental.launch
import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter

class MemberJoinListener: ListenerAdapter() {

    override fun onGuildMemberJoin(e: GuildMemberJoinEvent) {
        val guild = e.guild
        val user = e.user
        val member = e.member

        launch {
            WelcomeMessages.getWelcomeMessage(guild.idLong)?.let { message ->
                user.openPrivateChannel().queue { it.sendMessage(message).queue() }
            }

            AutoRole.getRole(guild.idLong)?.let {
                val role = guild.getRoleById(it) ?: return@let

                if(guild.selfMember.hasPermission(Permission.MANAGE_ROLES))
                    guild.controller.addRolesToMember(member, role).queue()
            }
        }
    }
}