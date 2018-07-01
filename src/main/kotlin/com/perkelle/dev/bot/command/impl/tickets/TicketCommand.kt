package com.perkelle.dev.bot.command.impl.tickets

import com.perkelle.dev.bot.command.CommandBuilder
import com.perkelle.dev.bot.command.CommandCategory
import com.perkelle.dev.bot.command.ICommand
import com.perkelle.dev.bot.command.PermissionCategory

class TicketCommand: ICommand {

    override fun register() {
        val cmdBuilder = CommandBuilder()
                .setName("tickets")
                .setDescription("General tickets command")
                .setAliases("ticket", "t")
                .setPremiumOnly(true)
                .setCategory(CommandCategory.TICKETS)
                .setPermission(PermissionCategory.TICKETS)
                .setExecutor(TicketHelpCommand())

        cmdBuilder.addChild(
                CommandBuilder(true, cmdBuilder)
                        .setName("setchannelcategory")
                        .setDescription("Sets the channel category which tickets will be contained in")
                        .setPremiumOnly(true)
                        .setCategory(CommandCategory.TICKETS)
                        .setPermission(PermissionCategory.ADMIN)
                        .setExecutor(TicketSetChannelCategoryCommand())
        ).addChild(
                CommandBuilder(true, cmdBuilder)
                        .setName("open")
                        .setDescription("Opens a new ticket")
                        .setAliases("new")
                        .setPremiumOnly(true)
                        .setCategory(CommandCategory.TICKETS)
                        .setPermission(PermissionCategory.TICKETS)
                        .setExecutor(TicketOpenCommand())
        ).addChild(
                CommandBuilder(true, cmdBuilder)
                        .setName("addmanager")
                        .setDescription("Assigns a user to be a ticket manager")
                        .setPremiumOnly(true)
                        .setCategory(CommandCategory.TICKETS)
                        .setPermission(PermissionCategory.ADMIN)
                        .setExecutor(TicketAddManagerCommand())
        ).addChild(
                CommandBuilder(true, cmdBuilder)
                        .setName("close")
                        .setDescription("Closes the ticket")
                        .setPremiumOnly(true)
                        .setCategory(CommandCategory.TICKETS)
                        .setPermission(PermissionCategory.TICKETS)
                        .setExecutor(TicketCloseCommand())
        ).addChild(
                CommandBuilder(true, cmdBuilder)
                        .setName("removemanager")
                        .setDescription("Revokes a user from the ticket manager role")
                        .setPremiumOnly(true)
                        .setCategory(CommandCategory.TICKETS)
                        .setPermission(PermissionCategory.ADMIN)
                        .setExecutor(TicketRemoveManagerCommand())
        ).addChild(
                CommandBuilder(true, cmdBuilder)
                        .setName("setarchivechannel")
                        .setDescription("Sets the channel that ticket archives should be posted to (or disables archiving if `off` is specified)")
                        .setPremiumOnly(true)
                        .setCategory(CommandCategory.TICKETS)
                        .setPermission(PermissionCategory.ADMIN)
                        .setExecutor(TicketSetArchiveChannelCommand())
        ).addChild(
                CommandBuilder(true, cmdBuilder)
                        .setName("setwelcomemessage")
                        .setDescription("Sets the message sent when a ticket is opened")
                        .setPremiumOnly(true)
                        .setCategory(CommandCategory.TICKETS)
                        .setPermission(PermissionCategory.ADMIN)
                        .setExecutor(TicketSetArchiveChannelCommand())
        )
    }
}