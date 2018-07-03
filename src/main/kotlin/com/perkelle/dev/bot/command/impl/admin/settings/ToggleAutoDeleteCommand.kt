package com.perkelle.dev.bot.command.impl.admin.settings

import com.perkelle.dev.bot.command.CommandBuilder
import com.perkelle.dev.bot.command.CommandCategory
import com.perkelle.dev.bot.command.ICommand
import com.perkelle.dev.bot.command.PermissionCategory
import com.perkelle.dev.bot.datastores.tables.settings.AutoDeleteMessages
import com.perkelle.dev.bot.utils.sendEmbed

class ToggleAutoDeleteCommand: ICommand {

    override fun register() {
        CommandBuilder()
                .setName("toggleautodelete")
                .setDescription("Toggles the automatic deletion of commands + bot responses")
                .setCategory(CommandCategory.SETTINGS)
                .setPermission(PermissionCategory.ADMIN)
                .setExecutor {
                    val autoDelete = AutoDeleteMessages.isAutoDelete(guild.idLong)

                    if(autoDelete) {
                        AutoDeleteMessages.disableAutoDelete(guild.idLong)
                        channel.sendEmbed("Auto Delete", "Disabled the automatic deletion of messages")
                    } else {
                        AutoDeleteMessages.enableAutoDelete(guild.idLong)
                        channel.sendEmbed("Auto Delete", "Enabled the automatic deletion of messages")
                    }
                }
    }
}