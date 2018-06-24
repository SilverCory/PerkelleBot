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

class KickCommand: ICommand {

    override fun register() {
        CommandBuilder()
                .setName("kick")
                .setDescription("Kicks a user and keeps their messages")
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

                    if(!guild.selfMember.canInteract(member) || !guild.selfMember.hasPermission(Permission.KICK_MEMBERS)) {
                        channel.sendEmbed("Moderation", "I can't kick ${member.asMention}", Colors.RED)
                        return@setExecutor
                    }

                    if(!sender.canInteract(member)) {
                        channel.sendEmbed("Moderation", "${member.asMention} is higher ranked than you. You do not have permission to kick them.")
                        return@setExecutor
                    }

                    val reason by lazy {
                        if(args.size == 1) "No reason specified"
                        else args.copyOfRange(1, args.size).joinToString(" ")
                    }

                    guild.controller.kick(member, "[Kick][${user.name}#${user.discriminator}] $reason").queue()

                    channel.sendEmbed("Moderation", "Kicked ${member.asMention}")
                    log(EventType.KICK, sender, member.user, reason)
                }
    }
}