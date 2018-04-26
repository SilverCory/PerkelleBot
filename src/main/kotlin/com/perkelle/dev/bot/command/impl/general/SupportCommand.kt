package com.perkelle.dev.bot.command.impl.general

import com.perkelle.dev.bot.command.CommandBuilder
import com.perkelle.dev.bot.command.CommandCategory
import com.perkelle.dev.bot.command.ICommand
import com.perkelle.dev.bot.command.PermissionCategory
import com.perkelle.dev.bot.getConfig
import com.perkelle.dev.bot.utils.deleteAfter

class SupportCommand: ICommand {

    override fun register() {
        CommandBuilder()
                .setName("support")
                .setDescription("Provides you with an invite to the support guild")
                .setCategory(CommandCategory.GENERAL)
                .setPermission(PermissionCategory.GENERAL)
                .setExecutor {
                    channel.sendMessage("Support guild: ${getConfig().getSupportGuild()}").queue { it.deleteAfter() }
                }
    }
}