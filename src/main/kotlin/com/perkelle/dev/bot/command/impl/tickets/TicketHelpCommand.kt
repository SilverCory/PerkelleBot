package com.perkelle.dev.bot.command.impl.tickets

import com.perkelle.dev.bot.command.CommandContext
import com.perkelle.dev.bot.command.Executor
import com.perkelle.dev.bot.getConfig
import com.perkelle.dev.bot.utils.sendEmbed

class TicketHelpCommand: Executor {

    override fun CommandContext.onExecute() {
        when { // Kinda hacky but was requested
            root.equals("new", true) || root.equals("open", true) -> TicketOpenCommand().execute(this)
            root.equals("close", true) -> TicketCloseCommand().execute(this)
            else -> channel.sendEmbed("Tickets", "Available commands:\n${commandBuilder.children.joinToString("\n") { "`${getConfig().getDefaultPrefix()}t ${it.name}`" }} ")
        }
    }
}