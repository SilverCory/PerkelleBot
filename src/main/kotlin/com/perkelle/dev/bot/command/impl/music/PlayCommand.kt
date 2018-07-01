package com.perkelle.dev.bot.command.impl.music

import com.perkelle.dev.bot.command.CommandBuilder
import com.perkelle.dev.bot.command.CommandCategory
import com.perkelle.dev.bot.command.ICommand
import com.perkelle.dev.bot.command.PermissionCategory
import com.perkelle.dev.bot.listeners.addReactCallback
import com.perkelle.dev.bot.listeners.removeCallback
import com.perkelle.dev.bot.managers.getWrapper
import com.perkelle.dev.bot.music.TrackLoader
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
                .setAliases("p", "sc", "soundcloud")
                .setCategory(CommandCategory.MUSIC)
                .setPermission(PermissionCategory.MUSIC)
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
                                args[0].matches(soundcloudPattern) -> RequestType.SOUNDCLOUD_URL
                                args[0].matches(urlPattern) -> RequestType.HTTP_URL
                                root.equals("sc", true) || root.equals("soundcloud", true) -> RequestType.SOUNDCLOUD_SEARCH
                                else -> RequestType.YOUTUBE_SEARCH
                            }
                        }
                    }

                    var request = args.joinToString(" ")
                    if(type == RequestType.YOUTUBE_SEARCH) request = "ytsearch: $request"
                    else if(type == RequestType.SOUNDCLOUD_SEARCH) request = "scsearch: $request"

                    val loadResult = TrackLoader.load(request, 5)
                    val tracks = loadResult.first

                    if(tracks.isEmpty()) {
                        channel.sendEmbed("Music", "Couldn't find a track. Perhaps the stream is offline?", Colors.RED)
                        return@setExecutor
                    }

                    when {
                        loadResult.second -> {
                            tracks.forEach { guild.getWrapper().musicManager.getScheduler().queue(it, channel, sender) }
                            channel.sendEmbed("Music", "Added all songs to the queue")
                        }
                        tracks.size == 1 -> {
                            val track = tracks[0]
                            guild.getWrapper().musicManager.getScheduler().queue(track, channel, sender)
                            channel.sendEmbed("Music", "Queued **${track.info.title}** (`${track.duration.formatMillis()}`)")
                        }
                        else -> channel.sendEmbed("Music", tracks.withIndex().joinToString("\n") { (index, track) -> "`${index + 1}` ${track.info.title} - ${track.info.author} `${track.duration.formatMillis()}`" }) { msg ->
                            msg.addReaction("c:443029836978716673").queue()
                            try {
                                if (tracks.size >= 2) msg.addReaction("c:443029859296346144").queue()
                                if (tracks.size >= 3) msg.addReaction("c:443029873464705034").queue()
                                if (tracks.size >= 4) msg.addReaction("c:443029887138267136").queue()
                                if (tracks.size >= 5) msg.addReaction("c:443029902556528640").queue()
                                msg.addReaction("c:443029916993191963").queue()
                            } catch(_: ErrorResponseException) {} //Thrown if the user reacts before all options are displayed (quite a lot of the time)

                            addReactCallback(msg.idLong) {
                                if (it.member != sender) return@addReactCallback

                                if(it.emote.id == null) return@addReactCallback
                                val track = when (it.emote.idLong) {
                                    443029836978716673 -> tracks[0]
                                    443029859296346144 -> tracks[1]
                                    443029873464705034 -> tracks[2]
                                    443029887138267136 -> tracks[3]
                                    443029902556528640-> tracks[4]
                                    443029916993191963 -> {
                                        removeCallback(msg.idLong)
                                        msg.delete().queue()
                                        null
                                    }
                                    else -> null
                                } ?: return@addReactCallback

                                msg.delete().queue()

                                guild.getWrapper().musicManager.getScheduler().queue(track, channel, sender)
                                channel.sendEmbed("Music", "Queued **${track.info.title}** (`${track.duration.formatMillis()}`)")
                            }
                        }
                    }
                }
    }

    enum class RequestType {
        YOUTUBE_URL,
        YOUTUBE_SEARCH,
        SOUNDCLOUD_URL,
        SOUNDCLOUD_SEARCH,
        HTTP_URL
    }
}