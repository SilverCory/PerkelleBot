package com.perkelle.dev.bot.command.impl.music

import com.perkelle.dev.bot.command.CommandBuilder
import com.perkelle.dev.bot.command.CommandCategory
import com.perkelle.dev.bot.command.ICommand
import com.perkelle.dev.bot.command.PermissionCategory
import com.perkelle.dev.bot.managers.getWrapper
import com.perkelle.dev.bot.music.TrackLoader
import com.perkelle.dev.bot.utils.Colors
import com.perkelle.dev.bot.utils.sendEmbed
import net.dv8tion.jda.core.Permission

class Top40Command: ICommand {

    override fun register() {
        CommandBuilder()
                .setName("top40")
                .setDescription("Queues the UK top 40 songs")
                .setCategory(CommandCategory.MUSIC)
                .setPermission(PermissionCategory.MUSIC_ADMIN)
                .setPremiumOnly(true)
                .setExecutor {
                    val audioManager = guild.audioManager
                    val mm = guild.getWrapper().musicManager

                    mm.getScheduler().looping = false

                    if(!audioManager.isConnected) {
                        if(!sender.voiceState.inVoiceChannel() || !guild.selfMember.hasPermission(sender.voiceState.channel, Permission.VOICE_CONNECT)) {
                            channel.sendEmbed("Music", "You are not in a voice channel (or I do not have permission to join yours)", Colors.RED)
                            return@setExecutor
                        }

                        audioManager.openAudioConnection(sender.voiceState.channel)
                    }

                    channel.sendEmbed("Music", "Queued the UK top 40")

                    val toSkip = mm.getScheduler().playing != null
                    mm.getScheduler().getQueue().clear()
                    val tracks = TrackLoader.load("https://www.youtube.com/playlist?list=PLx0sYbCqOb8Q_CLZC2BdBSKEEB59BOPUM").first
                    tracks.forEach { mm.getScheduler().queue(it, channel, sender) }

                    if(toSkip) mm.getScheduler().next()

                    mm.getScheduler().looping = true
                }
    }
}