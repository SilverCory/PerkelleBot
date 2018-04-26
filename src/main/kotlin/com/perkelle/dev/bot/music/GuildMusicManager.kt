package com.perkelle.dev.bot.music

import com.perkelle.dev.bot.PerkelleBot
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

class GuildMusicManager(val player: AudioPlayer, val guildId: Long, val shard: JDA): AudioEventAdapter() {

    private fun getGuild() = shard.getGuildById(guildId)

    init {
        player.addListener(this)
        getGuild().audioManager.sendingHandler = AudioPlayerSendHandler(player)
    }

    val queue = mutableListOf<AudioTrackWrapper>()
    var playing: AudioTrackWrapper? = null

    fun queue(audioTrackWrapper: AudioTrackWrapper) {
        queue.add(audioTrackWrapper)
        if(queue.size == 1 && playing == null) next()
        else audioTrackWrapper.channel.sendEmbed("Music", "Queued **${audioTrackWrapper.track.info.title}** `${audioTrackWrapper.track.duration.formatMillis()}`")
    }

    fun loadTracks(query: String, amount: Int = 5): List<AudioTrack> {
        val tracks = mutableListOf<AudioTrack>()

        //TODO: Make async
        PerkelleBot.instance.playerManager.loadItem(query, object: AudioLoadResultHandler {
            override fun loadFailed(exception: FriendlyException) {}

            override fun trackLoaded(track: AudioTrack) {
                tracks.add(track)
            }

            override fun noMatches() {}

            override fun playlistLoaded(playlist: AudioPlaylist) {
                playlist.tracks.withIndex().forEach {  (index, track) ->
                    if(index < amount) tracks.add(track)
                }
            }
        }).get()

        return tracks
    }

    fun next() {
        //TODO: Looping

        if(queue.size == 0) {
            getGuild().audioManager.closeAudioConnection()
            return
        }

        playing = queue.removeAt(0)
        player.startTrack(playing!!.track, false)

        playing!!.channel.sendEmbed("Music", "Now playing: **${playing!!.track.info.title}** `${playing!!.track.duration.formatMillis()}`", autoDelete = false) {
            launch {
                delay(playing!!.track.duration)
                it.delete().queue()
            }
        }
    }

    override fun onTrackEnd(player: AudioPlayer, track: AudioTrack, endReason: AudioTrackEndReason) {
        playing = null
        next()
    }
}