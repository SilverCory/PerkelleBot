package com.perkelle.dev.bot.listeners

import com.perkelle.dev.bot.managers.getWrapper
import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.events.guild.member.GuildMemberRoleAddEvent
import net.dv8tion.jda.core.events.guild.member.GuildMemberRoleRemoveEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter

class RoleListener: ListenerAdapter() {

    override fun onGuildMemberRoleAdd(e: GuildMemberRoleAddEvent) {
        val member = e.member
        val guild = e.guild
        val wrapper = guild.getWrapper()

        if(member.hasPermission(Permission.ADMINISTRATOR)) wrapper.admins.add(member)
    }

    override fun onGuildMemberRoleRemove(e: GuildMemberRoleRemoveEvent) {
        val member = e.member
        val guild = e.guild
        val wrapper = guild.getWrapper()

        if(!member.hasPermission(Permission.ADMINISTRATOR)) wrapper.admins.remove(member)
    }
}