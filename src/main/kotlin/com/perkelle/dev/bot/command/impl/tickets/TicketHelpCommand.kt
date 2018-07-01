package com.perkelle.dev.bot.command.impl.tickets

import com.perkelle.dev.bot.command.CommandContext
import com.perkelle.dev.bot.command.Executor
import com.perkelle.dev.bot.getConfig
import com.perkelle.dev.bot.utils.sendEmbed

class TicketHelpCommand: Executor {

    override fun CommandContext.onExecute() {
        channel.sendEmbed("Starboard", "Available commands:\n${commandBuilder.children.joinToString("\n") { "`${getConfig().getDefaultPrefix()}ticket ${it.name}`" }} ")
    }
}