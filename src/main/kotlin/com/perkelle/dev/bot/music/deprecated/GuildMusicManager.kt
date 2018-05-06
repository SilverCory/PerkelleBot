package com.perkelle.dev.bot.music.deprecated

import com.perkelle.dev.bot.PerkelleBot
import com.perkelle.dev.bot.datastores.tables.Volume
import com.perkelle.dev.bot.managers.getWrapper
import com.perkelle.dev.bot.music.AudioPlayerSendHandler
import com.perkelle.dev.bot.music.AudioTrackWrapper
import com.perkelle.dev.bot.utils.formatMillis
import com.perkelle.dev.bot.utils.sendEmbed
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import kotlinx.coroutines.experimental.launch
import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.entities.Member

@Deprecated("Use GuildAudioController")
class GuildMusicManager(val player: AudioPlayer, val guildId: Long, val shard: JDA): AudioEventAdapter() {

    private fun getGuild() = shard.getGuildById(guildId)

    init {
        player.addListener(this)
        getGuild().audioManager.sendingHandler = AudioPlayerSendHandler(player)

        launch {
            player.volume = Volume.getVolume(guildId)
        }
    }

    val queue = mutableListOf<AudioTrackWrapper>()
    var isLooping = false

    val voteSkips = mutableListOf<Member>()

    fun queue(audioTrackWrapper: AudioTrackWrapper, silent: Boolean = false) {
        queue.add(audioTrackWrapper)

        if(queue.size == 1) {
            next()
        }
        else if(!silent) audioTrackWrapper.channel.sendEmbed("Music", "Queued **${audioTrackWrapper.track.info.title}** `${audioTrackWrapper.track.duration.formatMillis()}`")
    }

    //Tracks -> Is YouTube playlist
    fun loadTracks(query: String, amount: Int = 5): Pair<List<AudioTrack>, Boolean> {
        val tracks = mutableListOf<AudioTrack>()
        var isYoutubePlaylist = false

        //TODO: Make async
        PerkelleBot.instance.playerManager.loadItem(query, object: AudioLoadResultHandler {
            override fun loadFailed(exception: FriendlyException) {}

            override fun trackLoaded(track: AudioTrack) {
                tracks.add(track)
            }

            override fun noMatches() {}

            override fun playlistLoaded(playlist: AudioPlaylist) {
                if(playlist.isSearchResult) {
                    for((index, track) in playlist.tracks.withIndex()) {
                        if (index < amount) tracks.add(track)
                        else break
                    }
                }
                else {
                    isYoutubePlaylist = true
                    tracks.addAll(playlist.tracks)
                }
            }
        }).get()

        return tracks to isYoutubePlaylist
    }

    fun next(disconnect: Boolean = true) {
        player.stopTrack()

        val track = queue.firstOrNull()
        if(track == null && disconnect) {
            getGuild().audioManager.closeAudioConnection()
            return
        }

        if(track == null) return

        player.startTrack(track.track, false)
    }

    override fun onTrackStart(player: AudioPlayer, aTrack: AudioTrack) {
        val track = queue[0]

        track.channel.sendEmbed("Music", "Now playing: **${track.track.info.title}** `${track.track.duration.formatMillis()}`", autoDelete = false) {
            getGuild().getWrapper().nowPlaying?.delete()?.queue()
            getGuild().getWrapper().nowPlaying = it
        }
    }

    override fun onTrackEnd(player: AudioPlayer, track: AudioTrack, endReason: AudioTrackEndReason) {
        if(queue.isNotEmpty()) {
            val old = queue.removeAt(0) //Pop from queue
            if(isLooping) queue.add(AudioTrackWrapper(old.track.makeClone(), old.channel, old.requester))
        }

        getGuild().getWrapper().nowPlaying?.delete()?.queue()
        voteSkips.clear()
        next()
    }
}