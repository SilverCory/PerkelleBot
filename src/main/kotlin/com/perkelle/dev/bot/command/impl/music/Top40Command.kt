package com.perkelle.dev.bot.command.impl.music

import com.perkelle.dev.bot.command.CommandBuilder
import com.perkelle.dev.bot.command.CommandCategory
import com.perkelle.dev.bot.command.ICommand
import com.perkelle.dev.bot.command.PermissionCategory
import com.perkelle.dev.bot.managers.getWrapper
import com.perkelle.dev.bot.music.AudioTrackWrapper
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
                    if(!audioManager.isConnected) {
                        if(!sender.voiceState.inVoiceChannel() || !guild.selfMember.hasPermission(sender.voiceState.channel, Permission.VOICE_CONNECT)) {
                            channel.sendEmbed("Music", "You are not in a voice channel (or I do not have permission to join yours)", Colors.RED)
                            return@setExecutor
                        }

                        audioManager.openAudioConnection(sender.voiceState.channel)
                    }

                    val mm = guild.getWrapper().musicManager
                    mm.queue.withIndex().forEach { (index, _) -> if(index != 0) mm.queue.removeAt(index) }

                    val tracks = mm.loadTracks("https://www.youtube.com/playlist?list=PLx0sYbCqOb8Q_CLZC2BdBSKEEB59BOPUM").first
                    tracks.forEach { mm.queue.add(AudioTrackWrapper(it.makeClone(), channel, sender)) }

                    if(mm.player.playingTrack != null) mm.player.stopTrack()
                    else mm.player.startTrack(mm.queue[0].track, false)

                    channel.sendEmbed("Music", "Queued the UK top 40")
                }
    }
}