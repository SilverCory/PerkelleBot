package com.perkelle.dev.bot.command.impl.music

import com.perkelle.dev.bot.command.CommandBuilder
import com.perkelle.dev.bot.command.CommandCategory
import com.perkelle.dev.bot.command.ICommand
import com.perkelle.dev.bot.command.PermissionCategory
import com.perkelle.dev.bot.managers.getWrapper
import com.perkelle.dev.bot.utils.Colors
import com.perkelle.dev.bot.utils.sendEmbed

class VoteSkipCommand: ICommand {

    override fun register() {
        CommandBuilder()
                .setName("voteskip")
                .setDescription("Vote to skip a song")
                .setCategory(CommandCategory.MUSIC)
                .setPermission(PermissionCategory.MUSIC)
                .setExecutor {
                    if(!sender.voiceState.inVoiceChannel() || !guild.selfMember.voiceState.inVoiceChannel() || guild.selfMember.voiceState.channel != sender.voiceState.channel) {
                        channel.sendEmbed("Music", "You must be in the same voice channel as the bot to vote to skip", Colors.RED)
                        return@setExecutor
                    }

                    val mm = guild.getWrapper().musicManager
                    val vc = guild.selfMember.voiceState.channel

                    if(mm.voteSkips.contains(sender)) {
                        channel.sendEmbed("Music", "You have already voted to skip", Colors.RED)
                        return@setExecutor
                    }

                    mm.voteSkips.add(sender)

                    if(mm.voteSkips.size >= Math.floor((vc.members.size - 1) / 2.0)) {
                        mm.getScheduler().next()
                        channel.sendEmbed("Music", "Over 50% of users voted to skip the song")
                    } else {
                        channel.sendEmbed("Music", "${sender.asMention} voted to skip the song `${mm.voteSkips.size} / ${Math.floor((vc.members.size - 1) / 2.0).toInt()}`")
                    }
                }
    }
}