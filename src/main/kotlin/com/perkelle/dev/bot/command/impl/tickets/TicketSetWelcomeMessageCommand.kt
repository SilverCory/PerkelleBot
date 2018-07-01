package com.perkelle.dev.bot.command.impl.tickets

import com.perkelle.dev.bot.command.CommandContext
import com.perkelle.dev.bot.command.Executor
import com.perkelle.dev.bot.datastores.tables.tickets.TicketWelcomeMessages
import com.perkelle.dev.bot.utils.Colors
import com.perkelle.dev.bot.utils.sendEmbed

class TicketSetWelcomeMessageCommand: Executor {

    override fun CommandContext.onExecute() {
        if(args.isEmpty()) {
            channel.sendEmbed("Tickets", "You must specify a message", Colors.RED)
            return
        }

        val message = args.joinToString(" ")
        if(message.length > 1000) {
            channel.sendEmbed("Tickets", "Welcome messages are limited to 1000 characters", Colors.RED)
            return
        }

        TicketWelcomeMessages.setMessage(guild.idLong, message)
    }
}