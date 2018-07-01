package com.perkelle.dev.bot.command.impl.starboard

import com.perkelle.dev.bot.command.CommandContext
import com.perkelle.dev.bot.command.Executor
import com.perkelle.dev.bot.datastores.tables.starboard.StarboardChannels
import com.perkelle.dev.bot.utils.sendEmbed

class StarboardOffCommand: Executor {

    override fun CommandContext.onExecute() {
        StarboardChannels.disableStarboard(guild.idLong)

        channel.sendEmbed("Starboard", "Disabled the starboard module")
    }
}