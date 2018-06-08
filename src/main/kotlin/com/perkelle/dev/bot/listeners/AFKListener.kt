package com.perkelle.dev.bot.listeners

import com.perkelle.dev.bot.command.impl.general.AfkCommand
import com.perkelle.dev.bot.utils.sendEmbed
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter

class AFKListener: ListenerAdapter() {

    override fun onGuildMessageReceived(e: GuildMessageReceivedEvent) {
        val msg = e.message
        val sender = e.member
        val channel = e.channel

        val afk = msg.mentionedMembers.filter { AfkCommand.afk.contains(it) }
        if(afk.isNotEmpty()) channel.sendEmbed("AFK", "The following users you mentioned are AFK: ${afk.joinToString(", ") { it.effectiveName } }")

        if(AfkCommand.afk.contains(sender)) {
            AfkCommand.afk.remove(sender)
            channel.sendEmbed("AFK", "Welcome back, ${sender.asMention}")
        }
    }
}