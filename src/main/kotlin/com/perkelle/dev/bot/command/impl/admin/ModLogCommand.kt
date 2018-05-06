package com.perkelle.dev.bot.command.impl.admin

import com.perkelle.dev.bot.command.CommandBuilder
import com.perkelle.dev.bot.command.CommandCategory
import com.perkelle.dev.bot.command.ICommand
import com.perkelle.dev.bot.command.PermissionCategory
import com.perkelle.dev.bot.datastores.tables.ModLogChannels
import com.perkelle.dev.bot.utils.sendEmbed

class ModLogCommand: ICommand {

    override fun register() {
        CommandBuilder()
                .setName("modlog")
                .setDescription("Sets the current channel to the mod-log channel")
                .setCategory(CommandCategory.SETTINGS)
                .setPermission(PermissionCategory.ADMIN)
                .setExecutor {
                    ModLogChannels.setChannel(channel)
                    channel.sendEmbed("Admin", "${channel.asMention} is now the mod-log channel")
                }
    }
}