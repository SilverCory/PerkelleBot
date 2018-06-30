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
        )
    }
}