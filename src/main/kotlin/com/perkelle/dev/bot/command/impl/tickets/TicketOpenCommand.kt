package com.perkelle.dev.bot.command.impl.tickets

import com.perkelle.dev.bot.command.CommandContext
import com.perkelle.dev.bot.command.Executor
import com.perkelle.dev.bot.datastores.tables.tickets.TicketChannelCategories
import com.perkelle.dev.bot.datastores.tables.tickets.TicketManagers
import com.perkelle.dev.bot.datastores.tables.tickets.TicketWelcomeMessages
import com.perkelle.dev.bot.datastores.tables.tickets.Tickets
import com.perkelle.dev.bot.getConfig
import com.perkelle.dev.bot.utils.Colors
import com.perkelle.dev.bot.utils.sendEmbed
import com.perkelle.dev.bot.utils.with
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.entities.TextChannel

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
        val id = Tickets.generateId(guild.idLong)

        val ticketChannel = category.createTextChannel("ticket-$id").complete() as TextChannel

        Tickets.createTicket(ticketChannel, id, sender)

        ticketChannel.sendMessage(
                EmbedBuilder()
                        .setTitle("Tickets")
                        .setColor(Colors.GREEN.denary)
                        .addField("Hey, ${sender.effectiveName}", TicketWelcomeMessages.getMessage(guild.idLong), false)
                        .addField("Subject", subject, false)
                        .build()
        ).queue()

        channel.sendEmbed("Tickets", "Opened a ticket: ${ticketChannel.asMention}")

        TicketManagers.getManagers(guild.idLong).mapNotNull { guild.getMemberById(it) }.with(sender).forEach { member ->
            ticketChannel.createPermissionOverride(member).setAllow(
                    Permission.VIEW_CHANNEL,
                    Permission.MESSAGE_READ,
                    Permission.MESSAGE_WRITE,
                    Permission.MESSAGE_ADD_REACTION,
                    Permission.MESSAGE_ATTACH_FILES,
                    Permission.MESSAGE_HISTORY,
                    Permission.MESSAGE_EMBED_LINKS
            ).queue()
        }

        ticketChannel.createPermissionOverride(guild.publicRole).setDeny(Permission.VIEW_CHANNEL, Permission.MESSAGE_READ).queue()
    }
}