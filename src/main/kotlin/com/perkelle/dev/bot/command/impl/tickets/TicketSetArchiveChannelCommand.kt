package com.perkelle.dev.bot.command.impl.tickets

import com.perkelle.dev.bot.command.CommandContext
import com.perkelle.dev.bot.command.Executor
import com.perkelle.dev.bot.datastores.tables.tickets.TicketArchiveChannels
import com.perkelle.dev.bot.utils.sendEmbed

class TicketSetArchiveChannelCommand: Executor {

    override fun CommandContext.onExecute() {
        if(args.isEmpty() || !args[0].equals("off", true)) {
            channel.sendEmbed("Tickets", "Changed ticket archive channel to ${channel.asMention}")
            TicketArchiveChannels.setChannel(guild.idLong, channel.idLong)
        } else {
            channel.sendEmbed("Tickets", "Disabled ticket archives")
            TicketArchiveChannels.disableArchiveChannel(guild.idLong)
        }
    }
}