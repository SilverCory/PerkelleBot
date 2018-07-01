package com.perkelle.dev.bot.command.impl.tickets

import com.perkelle.dev.bot.command.CommandContext
import com.perkelle.dev.bot.command.Executor
import com.perkelle.dev.bot.datastores.tables.tickets.TicketChannelCategories
import com.perkelle.dev.bot.utils.Colors
import com.perkelle.dev.bot.utils.sendEmbed

class TicketSetChannelCategoryCommand: Executor {

    override fun CommandContext.onExecute() {
        if(args.isEmpty()) {
            channel.sendEmbed("Tickets", "You need to specify a channel category name", Colors.RED)
            return
        }

        val name = args.joinToString(" ")
        val category = guild.categories.firstOrNull { it.name.equals(name, true) }

        if(category == null) {
            channel.sendEmbed("Tickets", "Invalid channel category", Colors.RED)
            return
        }

        channel.sendEmbed("Tickets", "Changed ticket channel category to `${category.name}`")
        TicketChannelCategories.setCategory(guild.idLong, category.idLong)
    }
}