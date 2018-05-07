package com.perkelle.dev.bot.command.impl.moderator

import com.perkelle.dev.bot.command.CommandBuilder
import com.perkelle.dev.bot.command.CommandCategory
import com.perkelle.dev.bot.command.ICommand
import com.perkelle.dev.bot.command.PermissionCategory
import com.perkelle.dev.bot.logging.EventType
import com.perkelle.dev.bot.logging.log
import com.perkelle.dev.bot.utils.Colors
import com.perkelle.dev.bot.utils.sendEmbed

class SoftbanCommand: ICommand {

    override fun register() {
        CommandBuilder()
                .setName("softban")
                .setDescription("Kicks a user and deletes their messages")
                .setCategory(CommandCategory.MODERATION)
                .setPermission(PermissionCategory.MODERATOR)
                .setExecutor {
                    if(message.mentionedUsers.isEmpty()) {
                        channel.sendEmbed("Moderation", "You need to tag a user to kick", Colors.RED)
                        return@setExecutor
                    }

                    val member = message.mentionedMembers[0]
                    if(member == null) {
                        channel.sendEmbed("Moderation", "You need to tag a user to kick", Colors.RED)
                        return@setExecutor
                    }

                    if(!guild.selfMember.canInteract(member)) {
                        channel.sendEmbed("Moderation", "I can't ban ${member.asMention}", Colors.RED)
                        return@setExecutor
                    }

                    val reason by lazy {
                        if(args.size == 1) "No reason specified"
                        else args.copyOfRange(1, args.size).joinToString(" ")
                    }

                    guild.controller.ban(member.user, 7, "[Softban][${user.name}#${user.discriminator}] $reason").queue()
                    guild.controller.unban(member.user.id).queue()

                    channel.sendEmbed("Moderation", "Softbanned ${member.asMention}")
                    log(EventType.SOFTBAN, sender, member.user, reason)
                }
    }
}