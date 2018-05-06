package com.perkelle.dev.bot.listeners

import com.perkelle.dev.bot.checks.CheckManager
import com.perkelle.dev.bot.checks.MessageContext
import kotlinx.coroutines.experimental.launch
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter

/**
 * Used for checks. Different to commands
 */
class MessageListener: ListenerAdapter() {

    override fun onGuildMessageReceived(e: GuildMessageReceivedEvent) {
        val sender = e.member
        val user = sender.user
        val guild = e.guild
        val channel = e.channel
        val message = e.message
        val content = message.contentStripped

        val context = MessageContext(sender, user, guild, channel, message, content)

        launch {
            val violated = CheckManager.checks.filter { check -> content.split(" ").any { check.regex.matches(it) } }
            violated.filter { it.isEnabled(guild.idLong) }.forEach { it.handle(context) }
        }
    }
}