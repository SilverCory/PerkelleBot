package com.perkelle.dev.bot.command.impl.general

import com.perkelle.dev.bot.command.CommandBuilder
import com.perkelle.dev.bot.command.CommandCategory
import com.perkelle.dev.bot.command.ICommand
import com.perkelle.dev.bot.command.PermissionCategory
import com.perkelle.dev.bot.getConfig
import com.perkelle.dev.bot.utils.deleteAfter

class InviteCommand: ICommand {

    override fun register() {
        CommandBuilder()
                .setName("invite")
                .setDescription("Sends you a link to the bot's invite page")
                .setCategory(CommandCategory.GENERAL)
                .setPermission(PermissionCategory.GENERAL)
                .setExecutor {
                    channel.sendMessage("Invite the bot: <${getConfig().getInviteLink()}>").queue { it.deleteAfter() }
                }
    }
}