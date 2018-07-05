package com.perkelle.dev.bot.command.impl.tickets

import com.perkelle.dev.bot.command.CommandContext
import com.perkelle.dev.bot.command.Executor
import com.perkelle.dev.bot.datastores.tables.tickets.TicketManagers
import com.perkelle.dev.bot.utils.Colors
import com.perkelle.dev.bot.utils.sendEmbed

class TicketRemoveManagerCommand: Executor {

    override fun CommandContext.onExecute() {
        if(message.mentionedMembers.isEmpty()) {
            channel.sendEmbed("Tickets", "You need to tag the user that will revoked from the manager role", Colors.RED)
            return
        }

        val manager = message.mentionedMembers[0]
        if(!TicketManagers.isManager(manager)) {
            channel.sendEmbed("Tickets", "${manager.asMention} is not a manager")
            return
        }

        TicketManagers.removeManager(guild.idLong, manager.user.idLong)
        channel.sendEmbed("Tickets", "Removed ${manager.asMention} from the manager role")
    }
}