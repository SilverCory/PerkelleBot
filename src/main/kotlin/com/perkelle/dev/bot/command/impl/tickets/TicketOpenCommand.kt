package com.perkelle.dev.bot.command.impl.tickets

import com.perkelle.dev.bot.command.CommandContext
import com.perkelle.dev.bot.command.Executor
import com.perkelle.dev.bot.datastores.tables.tickets.TicketChannelCategories
import com.perkelle.dev.bot.getConfig
import com.perkelle.dev.bot.utils.Colors
import com.perkelle.dev.bot.utils.sendEmbed
import net.dv8tion.jda.core.Permission

class TicketOpenCommand: Executor {

    override fun CommandContext.onExecute() {
        val subject =
                if(args.isEmpty()) "No subject given"
                else args.joinToString(" ")

        if(!guild.selfMember.hasPermission(Permission.MANAGE_CHANNEL) || !guild.selfMember.hasPermission(Permission.MANAGE_PERMISSIONS)) {
            channel.sendEmbed("Tickets", "I need the `Manage Channels` and `Manage Permissions` permissions to open a ticket", Colors.RED)
            return
        }

        val categoryId = TicketChannelCategories.getCategory(guild.idLong)
        if(categoryId == null || guild.categories.none { it.idLong == categoryId }) {
            channel.sendEmbed("Tickets", "The server owner needs to set the ticket channel category (`${getConfig().getDefaultPrefix()}tickets setchannelcategory`)", Colors.RED)
            return
        }

        val category = guild.categories.first { it.idLong == categoryId }
        category.createTextChannel("ticket")
    }
}