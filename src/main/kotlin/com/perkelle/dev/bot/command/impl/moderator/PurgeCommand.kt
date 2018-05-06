package com.perkelle.dev.bot.command.impl.moderator

import com.perkelle.dev.bot.command.CommandBuilder
import com.perkelle.dev.bot.command.CommandCategory
import com.perkelle.dev.bot.command.ICommand
import com.perkelle.dev.bot.command.PermissionCategory
import com.perkelle.dev.bot.logging.logPurge
import com.perkelle.dev.bot.utils.Colors
import com.perkelle.dev.bot.utils.sendEmbed

class PurgeCommand: ICommand {

    override fun register() {
        CommandBuilder()
                .setName("purge")
                .setDescription("Removes the last X messages")
                .setCategory(CommandCategory.MODERATION)
                .setPermission(PermissionCategory.MODERATOR)
                .setExecutor {
                    if(args.isEmpty() || args[0].toIntOrNull() == null || args[0].toInt() < 1) {
                        channel.sendEmbed("Moderation", "You need to specify an amount of messages to remove", Colors.RED)
                        return@setExecutor
                    }

                    val amount = args[0].toInt() + 1
                    var toRemove = amount

                    while(toRemove > 0) {
                        when {
                            toRemove >= 100 -> {
                                channel.history.retrievePast(100).queue { channel.deleteMessages(it) }
                                toRemove -= 100
                            }

                            else -> {
                                channel.history.retrievePast(toRemove).queue { channel.deleteMessages(it) }
                                toRemove = 0
                            }
                        }
                    }

                    channel.sendEmbed("Moderation", "Purged `$amount` messages")
                    logPurge(sender, channel, amount)
                }
    }
}