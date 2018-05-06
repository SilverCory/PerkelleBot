package com.perkelle.dev.bot.command.impl.moderator

import com.perkelle.dev.bot.Constants
import com.perkelle.dev.bot.command.CommandBuilder
import com.perkelle.dev.bot.command.CommandCategory
import com.perkelle.dev.bot.command.ICommand
import com.perkelle.dev.bot.command.PermissionCategory
import com.perkelle.dev.bot.logging.EventType
import com.perkelle.dev.bot.logging.log
import com.perkelle.dev.bot.utils.Colors
import com.perkelle.dev.bot.utils.sendEmbed

class BanCommand: ICommand {

    override fun register() {
        CommandBuilder()
                .setName("ban")
                .setDescription("Permanently bans a user from the server")
                .setCategory(CommandCategory.MODERATION)
                .setPermission(PermissionCategory.MODERATOR)
                .setExecutor {
                    if(args.isEmpty() || !Constants.MENTION_REGEX.matches(args[0])) {
                        channel.sendEmbed("Moderation", "You need to tag a user to ban", Colors.RED)
                        return@setExecutor
                    }

                    val id = Constants.MENTION_REGEX.find(args[0])!!.groups[1]?.value?.toLongOrNull()
                    if(id == null) {
                        channel.sendEmbed("Moderation", "You need to tag a user to ban", Colors.RED)
                        return@setExecutor
                    }

                    val member = guild.getMemberById(id)
                    if(member == null) {
                        channel.sendEmbed("Moderation", "You need to tag a user to ban", Colors.RED)
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

                    guild.controller.ban(member.user, 7, "[Ban][${user.name}#${user.discriminator}] $reason").queue()

                    channel.sendEmbed("Moderation", "Banned ${member.asMention}")
                    log(EventType.BAN, sender, member.user, reason)
                }
    }
}