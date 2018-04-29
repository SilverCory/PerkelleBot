package com.perkelle.dev.bot.music

import com.perkelle.dev.bot.PerkelleBot
import com.perkelle.dev.bot.datastores.tables.Volume
import com.perkelle.dev.bot.utils.formatMillis
import com.perkelle.dev.bot.utils.sendEmbed
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.entities.Member

class GuildMusicManager(val player: AudioPlayer, val guildId: Long, val shard: JDA): AudioEventAdapter() {

    private fun getGuild() = shard.getGuildById(guildId)

    init {
        player.addListener(this)
        getGuild().audioManager.sendingHandler = AudioPlayerSendHandler(player)

        Volume.getVolume(getGuild().idLong) {
            player.volume = it
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

    fun next() {
        if(player.playingTrack != null) player.stopTrack()

        val track = queue.firstOrNull()
        if(track == null) {
            getGuild().audioManager.closeAudioConnection()
            return
        }

        player.startTrack(track.track.makeClone(), false)
    }

    override fun onTrackStart(player: AudioPlayer, aTrack: AudioTrack) {
        val track = queue[0]

        track.channel.sendEmbed("Music", "Now playing: **${track.track.info.title}** `${track.track.duration.formatMillis()}`", autoDelete = false) {
            launch {
                delay(track.track.duration)
                it.delete().queue()
            }
        }
    }

    override fun onTrackEnd(player: AudioPlayer, track: AudioTrack, endReason: AudioTrackEndReason) {
        val wrapped = queue.removeAt(0) //Pop from queue
        if(isLooping) queue.add(AudioTrackWrapper(wrapped.track.makeClone(), wrapped.channel, wrapped.requester))

        voteSkips.clear()
        next()
    }
}