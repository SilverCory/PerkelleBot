package com.perkelle.dev.bot.command.impl.moderator

import com.perkelle.dev.bot.command.CommandBuilder
import com.perkelle.dev.bot.command.CommandCategory
import com.perkelle.dev.bot.command.ICommand
import com.perkelle.dev.bot.command.PermissionCategory
import com.perkelle.dev.bot.datastores.tables.BlacklistedMembers
import com.perkelle.dev.bot.utils.Colors
import com.perkelle.dev.bot.utils.sendEmbed

class BlacklistCommand: ICommand {

    override fun register() {
        CommandBuilder()
                .setName("blacklist")
                .setDescription("Blacklist a user from using the bot")
                .setAliases("ignore", "unblacklist")
                .setCategory(CommandCategory.MODERATION)
                .setPermission(PermissionCategory.MODERATOR)
                .setExecutor {
                    if (message.mentionedMembers.isEmpty()) {
                        channel.sendEmbed("Blacklist", "You need to tag users to blacklist", Colors.RED)
                        return@setExecutor
                    }

                    message.mentionedMembers.forEach {
                        if (!sender.canInteract(it)) {
                            channel.sendEmbed("Blacklist", "${it.asMention} is a superior to you, you cannot blacklist them", Colors.RED)
                            return@setExecutor
                        }

                        if (!guild.selfMember.canInteract(it)) {
                            channel.sendEmbed("Blacklist", "${it.asMention} is a superior to me, I cannot blacklist them", Colors.RED)
                            return@setExecutor
                        }

                        if (it == sender) {
                            channel.sendEmbed("Blacklist", "You can't blacklist yourself", Colors.RED)
                            return@setExecutor
                        }

                        val blacklisted = BlacklistedMembers.isBlacklisted(it)

                        if (blacklisted) {
                            channel.sendEmbed("Blacklist", "Unblacklisted ${it.asMention}")
                            BlacklistedMembers.removeBlacklist(guild.idLong, it.user.idLong)
                        } else {
                            channel.sendEmbed("Blacklist", "Blacklisted ${it.asMention}")
                            BlacklistedMembers.addBlacklist(guild.idLong, it.user.idLong)
                        }
                    }
                }
    }
}