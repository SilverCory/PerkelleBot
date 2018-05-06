package com.perkelle.dev.bot.command.impl.admin

import com.perkelle.dev.bot.command.CommandBuilder
import com.perkelle.dev.bot.command.CommandCategory
import com.perkelle.dev.bot.command.ICommand
import com.perkelle.dev.bot.command.PermissionCategory
import com.perkelle.dev.bot.datastores.tables.WelcomeMessages
import com.perkelle.dev.bot.utils.sendEmbed

class WelcomeCommand: ICommand {

    override fun register() {
        CommandBuilder()
                .setName("setwelcome")
                .setDescription("Sets the welcome message")
                .setCategory(CommandCategory.SETTINGS)
                .setPermission(PermissionCategory.ADMIN)
                .setExecutor {
                    if(args.isEmpty()) {
                        channel.sendEmbed("Admin", "Disabled welcome message")
                        WelcomeMessages.setWelcomeMessage(guild.idLong, null)
                        return@setExecutor
                    }

                    val welcomeMessage = args.joinToString(" ")
                    if(welcomeMessage.length > 2000) {
                        channel.sendEmbed("Admin", "Due to a restriction imposed by Discord, the welcome message can't be over 2,000 characters")
                        return@setExecutor
                    }

                    WelcomeMessages.setWelcomeMessage(guild.idLong, welcomeMessage)
                    channel.sendEmbed("Admin", "Updated welcome message")
                }
    }
}