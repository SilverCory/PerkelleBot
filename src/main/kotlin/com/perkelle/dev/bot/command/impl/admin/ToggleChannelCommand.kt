package com.perkelle.dev.bot.command.impl.admin

import com.perkelle.dev.bot.command.CommandBuilder
import com.perkelle.dev.bot.command.CommandCategory
import com.perkelle.dev.bot.command.ICommand
import com.perkelle.dev.bot.command.PermissionCategory
import com.perkelle.dev.bot.datastores.tables.DisabledChannels
import com.perkelle.dev.bot.managers.getWrapper
import com.perkelle.dev.bot.utils.sendEmbed

class ToggleChannelCommand: ICommand {

    override fun register() {
        CommandBuilder()
                .setName("togglechannel")
                .setDescription("Stops the bot from using a channel")
                .setCategory(CommandCategory.SETTINGS)
                .setPermission(PermissionCategory.ADMIN)
                .setExecutor {
                    val wrapper = guild.getWrapper()

                    DisabledChannels.isDisabled(channel.idLong) { disabled ->
                        DisabledChannels.setDisabled(channel.idLong, !disabled)

                        if(!disabled) {
                            channel.sendEmbed("Toggle Channel","Stopped listening in `${channel.name}`")
                            wrapper.disabledChannels.add(channel.idLong)
                        } else {
                            channel.sendEmbed("Toggle Channel","Started listening in `${channel.name}`")
                            wrapper.disabledChannels.remove(channel.idLong)
                        }
                    }
                }
    }
}