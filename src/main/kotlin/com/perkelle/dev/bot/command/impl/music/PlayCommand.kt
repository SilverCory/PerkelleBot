package com.perkelle.dev.bot.command.impl.music

import com.perkelle.dev.bot.command.CommandBuilder
import com.perkelle.dev.bot.command.ICommand
import com.perkelle.dev.bot.listeners.addReactCallback
import com.perkelle.dev.bot.managers.getWrapper
import com.perkelle.dev.bot.music.AudioTrackWrapper
import com.perkelle.dev.bot.utils.Colors
import com.perkelle.dev.bot.utils.formatMillis
import com.perkelle.dev.bot.utils.sendEmbed
import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.exceptions.ErrorResponseException

class PlayCommand: ICommand {

    private val youtubePattern = Regex("https?://(m|www\\.?)youtu(be.com|.be)/watch\\?v=([\\w]+)")
    private val urlPattern = Regex("https?://(.*\\.)?([\\w]+)\\.([\\w]+)(/.*)?")
    private val soundcloudPattern = Regex("(?:https?)://(?:www.)?soundcloud.com/(?:.*)")

    override fun register() {
        CommandBuilder()
                .setName("play")
                .setDescription("Play a song from a URL or YouTube")
                .setAliases("p")
                .setExecutor {
                    if(args.isEmpty()) {
                        channel.sendEmbed("Music", "You need to specify a search term or URL")
                        return@setExecutor
                    }

                    val audioManager = guild.audioManager
                    if(!audioManager.isConnected) {
                        if(!sender.voiceState.inVoiceChannel() || !guild.selfMember.hasPermission(sender.voiceState.channel, Permission.VOICE_CONNECT)) {
                            channel.sendEmbed("Music", "You are not in a voice channel (or I do not have permission to join yours)", Colors.RED)
                            return@setExecutor
                        }

                        audioManager.openAudioConnection(sender.voiceState.channel)
                    }

                    val type by lazy {
                        if(args.size > 1) RequestType.YOUTUBE_SEARCH
                        else {
                            when {
                                args[0].matches(youtubePattern) -> RequestType.YOUTUBE_URL
                                args[0].matches(urlPattern) -> RequestType.HTTP_URL
                                else -> RequestType.YOUTUBE_SEARCH
                            }
                        }
                    }

                    var request = args.joinToString(" ")
                    if(type == RequestType.YOUTUBE_SEARCH) request = "ytsearch: $request"

                    val loadResult = guild.getWrapper().musicManager.loadTracks(request, 5)
                    val tracks = loadResult.first

                    if(tracks.isEmpty()) {
                        channel.sendEmbed("Music", "Couldn't find a track. Perhaps the stream is offline?", Colors.RED)
                        return@setExecutor
                    }

                    if(loadResult.second) {
                        tracks.forEach { guild.getWrapper().musicManager.queue(AudioTrackWrapper(it, channel, sender), true) }
                        channel.sendEmbed("Music", "Added all songs to the queue")
                    }
                    else {
                        channel.sendEmbed("Music", tracks.withIndex().joinToString("\n") { (index, track) -> "`${index + 1}` ${track.info.title} - ${track.info.author} `${track.duration.formatMillis()}`" }) { msg ->
                            msg.addReaction("\u0031\u20E3").queue()
                            try {
                                if (tracks.size >= 2) msg.addReaction("\u0032\u20E3").queue()
                                if (tracks.size >= 3) msg.addReaction("\u0033\u20E3").queue()
                                if (tracks.size >= 4) msg.addReaction("\u0034\u20E3").queue()
                                if (tracks.size >= 5) msg.addReaction("\u0035\u20E3").queue()
                            } catch(_: ErrorResponseException) {} //Thrown if the user reacts before all options are displayed (quite a lot of the time)

                            addReactCallback(msg.idLong) {
                                if (it.member != sender) return@addReactCallback

                                val track = when (it.emote.name) {
                                    "1⃣" -> tracks[0]
                                    "2⃣" -> tracks[1]
                                    "3⃣" -> tracks[2]
                                    "4⃣" -> tracks[3]
                                    "5⃣" -> tracks[4]
                                    else -> null
                                } ?: return@addReactCallback

                                msg.delete().queue()

                                guild.getWrapper().musicManager.queue(AudioTrackWrapper(track, channel, sender))
                            }
                        }
                    }
                }
    }

    enum class RequestType {
        YOUTUBE_URL,
        YOUTUBE_SEARCH,
        HTTP_URL
    }
}