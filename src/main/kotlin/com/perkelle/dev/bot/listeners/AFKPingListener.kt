package com.perkelle.dev.bot.listeners

import com.perkelle.dev.bot.command.impl.general.AfkCommand
import com.perkelle.dev.bot.utils.sendEmbed
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter

class AFKPingListener: ListenerAdapter() {

    override fun onGuildMessageReceived(e: GuildMessageReceivedEvent) {
        val msg = e.message
        val afk = msg.mentionedMembers.filter { AfkCommand.afk.contains(it) }
        if(afk.isNotEmpty()) e.channel.sendEmbed("AFK", "The following users you mentioned are AFK: ${afk.joinToString(", ") { it.effectiveName } }")
    }
}