package com.perkelle.dev.bot.command.impl.starboard

import com.perkelle.dev.bot.command.CommandContext
import com.perkelle.dev.bot.command.Executor
import com.perkelle.dev.bot.datastores.tables.starboard.StarboardChannels
import com.perkelle.dev.bot.utils.sendEmbed

class StarboardSetChannelCommand: Executor {

    override fun CommandContext.onExecute() {
        StarboardChannels.setStarboardChannel(guild.idLong, channel.idLong)

        channel.sendEmbed("Starboard", "Set the starboard channel to ${channel.asMention}")
    }
}