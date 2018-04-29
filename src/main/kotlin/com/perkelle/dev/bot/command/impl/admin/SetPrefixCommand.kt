package com.perkelle.dev.bot.command.impl.admin

import com.perkelle.dev.bot.command.CommandBuilder
import com.perkelle.dev.bot.command.CommandCategory
import com.perkelle.dev.bot.command.ICommand
import com.perkelle.dev.bot.command.PermissionCategory
import com.perkelle.dev.bot.datastores.getSQLBackend
import com.perkelle.dev.bot.managers.getWrapper
import com.perkelle.dev.bot.utils.Colors
import com.perkelle.dev.bot.utils.sendEmbed

class SetPrefixCommand: ICommand {

    override fun register() {
        CommandBuilder()
                .setName("setprefix")
                .setDescription("Changes the prefix of the bot")
                .setCategory(CommandCategory.SETTINGS)
                .setPermission(PermissionCategory.ADMIN)
                .setExecutor {
                    if(args.isEmpty()) {
                        channel.sendEmbed("Set Prefix", "You need to specify a prefix", Colors.RED)
                        return@setExecutor
                    }

                    val prefix = args.first()
                    if(prefix.length > 4) {
                        channel.sendEmbed("Set Prefix", "Prefixes must be less than 5 characters", Colors.RED)
                        return@setExecutor
                    }

                    guild.getWrapper().prefix = prefix
                    getSQLBackend().setPrefix(guild.idLong, prefix)
                    channel.sendEmbed("Set Prefix", "Updated the guild prefix to `$prefix`. You may still use `p!` though.")
                }
    }
}