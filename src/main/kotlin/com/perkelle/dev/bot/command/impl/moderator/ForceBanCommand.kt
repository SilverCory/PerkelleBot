package com.perkelle.dev.bot.command.impl.moderator

import com.perkelle.dev.bot.command.CommandBuilder
import com.perkelle.dev.bot.command.CommandCategory
import com.perkelle.dev.bot.command.ICommand
import com.perkelle.dev.bot.command.PermissionCategory
import com.perkelle.dev.bot.logging.EventType
import com.perkelle.dev.bot.logging.log
import com.perkelle.dev.bot.utils.Colors
import com.perkelle.dev.bot.utils.sendEmbed
import net.dv8tion.jda.core.Permission

class ForceBanCommand: ICommand {

    override fun register() {
        CommandBuilder()
                .setName("ban")
                .setDescription("Permanently bans a user from the server. Takes a user ID, as opposed to a user mention.")
                .setCategory(CommandCategory.MODERATION)
                .setPermission(PermissionCategory.MODERATOR)
                .setAliases("hackban")
                .setExecutor {
                    if(args.isEmpty()) {
                        channel.sendEmbed("Moderation", "You need to specify a user ID to ban", Colors.RED)
                        return@setExecutor
                    }

                    val id = args[0]
                    if(id.toLongOrNull() == null) {
                        channel.sendEmbed("Moderation", "You need to specify a user ID to ban", Colors.RED)
                        return@setExecutor
                    }

                    val member = guild.getMemberById(id)
                    if(member != null) {
                        if(!guild.selfMember.canInteract(member) || !guild.selfMember.hasPermission(Permission.BAN_MEMBERS)) {
                            channel.sendEmbed("Moderation", "I don't have permission to ban ${member.asMention}", Colors.RED)
                            return@setExecutor
                        }

                        if(!sender.canInteract(member)) {
                            channel.sendEmbed("Moderation", "${member.asMention} is higher ranked than you. You do not have permission to ban them.")
                            return@setExecutor
                        }
                    }

                    val reason by lazy {
                        if(args.size == 1) "No reason specified"
                        else args.copyOfRange(1, args.size).joinToString(" ")
                    }

                    guild.controller.ban(id, 7, "[Force Ban][${user.name}#${user.discriminator}] $reason").queue()

                    channel.sendEmbed("Moderation", "Force banned ${member.asMention}")
                    log(EventType.BAN, sender, member.user, reason)
                }
    }
}