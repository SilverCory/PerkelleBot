package com.perkelle.dev.bot.command.impl.starboard

import com.perkelle.dev.bot.command.CommandContext
import com.perkelle.dev.bot.command.Executor
import com.perkelle.dev.bot.getConfig
import com.perkelle.dev.bot.utils.sendEmbed

class StarboardHelpCommand: Executor {

    override fun CommandContext.onExecute() {
        channel.sendEmbed("Starboard", "Available commands:\n${commandBuilder.children.joinToString("\n") { "`${getConfig().getDefaultPrefix()}starboard ${it.name}`" }} ")
    }
}