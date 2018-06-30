package com.perkelle.dev.bot.logging

import com.perkelle.dev.bot.datastores.tables.settings.ModLogChannels
import com.perkelle.dev.bot.utils.Colors
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.entities.*

fun logPurge(mod: Member, channel: TextChannel, amount: Int) {
    ModLog.log(channel.guild, EmbedBuilder()
            .setTitle("Purge")
            .setColor(Colors.LIME)
            .addField("Moderator", mod.asMention, true)
            .addField("Channel", channel.asMention, true)
            .addField("Messages", amount.toString(), true)
            .build())
}

fun log(type: EventType, mod: Member, target: User, reason: String) {
    ModLog.log(mod.guild, EmbedBuilder()
            .setTitle(type.name.capitalize())
            .setColor(when(type) {
                EventType.KICK -> Colors.BLUE
                EventType.SOFTBAN -> Colors.ORANGE
                else -> Colors.RED
            })
            .addField("Moderator", mod.asMention, true)
            .addField("Target", target.asMention, true)
            .addField("Reason", reason, true)
            .build())
}

object ModLog {

    fun log(guild: Guild, message: MessageEmbed) {
        val channelId = ModLogChannels.getChannel(guild.idLong) ?: return
        val channel = guild.getTextChannelById(channelId) ?: return

        channel.sendMessage(message).queue()
    }
}

enum class EventType {
    KICK, SOFTBAN, BAN
}