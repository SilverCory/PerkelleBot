package com.perkelle.dev.bot.command.impl.tickets

import com.perkelle.dev.bot.command.CommandContext
import com.perkelle.dev.bot.command.Executor
import com.perkelle.dev.bot.datastores.tables.tickets.TicketArchiveChannels
import com.perkelle.dev.bot.datastores.tables.tickets.TicketManagers
import com.perkelle.dev.bot.datastores.tables.tickets.Tickets
import com.perkelle.dev.bot.utils.Colors
import com.perkelle.dev.bot.utils.sendEmbed
import net.dv8tion.jda.core.MessageBuilder
import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.entities.MessageChannel
import java.text.SimpleDateFormat
import java.util.*

class TicketCloseCommand: Executor {

    private val dateFormat = SimpleDateFormat("HH':'mm dd '/' MM '/' yyyy")

    override fun CommandContext.onExecute() {
        if(!Tickets.isTicketChannel(channel.idLong)) {
            channel.sendEmbed("Tickets", "This channel is not a ticket channel", Colors.RED)
            return
        }

        if(!TicketManagers.isManager(sender) && Tickets.getTicket(channel.idLong)?.owner != sender.user.idLong) {
            channel.sendEmbed("Tickets", "Only managers and the ticket owner can close the ticket", Colors.RED)
            return
        }

        if(!guild.selfMember.hasPermission(Permission.MANAGE_CHANNEL)) {
            channel.sendEmbed("Tickets", "I do not have permission to delete this channel", Colors.RED)
            return
        }

        TicketArchiveChannels.getChannel(guild.idLong)?.let {
            val archiveChannel = guild.getTextChannelById(it) ?: return@let
            val ticketChannel = channel as MessageChannel

            channel.sendEmbed("Tickets", "Processing...")

            val sb = StringBuilder()

            val mh = ticketChannel.history
            while(true) {
                val history = mh.retrievePast(100).complete()

                if(history.isEmpty()) break
                else {
                    history.reversed().forEach { msg ->
                        sb.append("[${dateFormat.format(Date(msg.creationTime.toInstant().toEpochMilli()))}][${msg.id}] ${msg.member.effectiveName}: ${msg.contentRaw}\n")
                    }
                }
            }

            archiveChannel.sendFile(sb.toString().byteInputStream(), "${ticketChannel.name}.txt", MessageBuilder()
                    .setContent("Archive of `#${ticketChannel.name}` (closed by ${sender.effectiveName}#${sender.user.discriminator})")
                    .build()
            ).queue()
        }

        Tickets.setClosed(channel.idLong)
        channel.delete().reason("Ticket closed by ${sender.effectiveName}").queue()
    }
}