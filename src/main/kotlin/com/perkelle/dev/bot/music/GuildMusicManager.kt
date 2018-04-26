package com.perkelle.dev.bot.music

import com.perkelle.dev.bot.PerkelleBot
import com.perkelle.dev.bot.command.datastores.getSQLBackend
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
import java.util.concurrent.LinkedBlockingQueue

class GuildMusicManager(val player: AudioPlayer, val guildId: Long, val shard: JDA): AudioEventAdapter() {

    private fun getGuild() = shard.getGuildById(guildId)

    init {
        player.addListener(this)
        getGuild().audioManager.sendingHandler = AudioPlayerSendHandler(player)

        getSQLBackend().getVolume(getGuild().idLong) {
            player.volume = it
        }
    }

    val queue = LinkedBlockingQueue<AudioTrackWrapper>()
    var isLooping = false

    fun queue(audioTrackWrapper: AudioTrackWrapper) {
        queue.add(audioTrackWrapper)

        if(queue.size == 1) next()
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
        if(player.playingTrack != null) player.stopTrack()

        val track = queue.peek()
        if(track == null) {
            getGuild().audioManager.closeAudioConnection()
            return
        }

        track.channel.sendEmbed("Music", "Now playing: **${track.track.info.title}** `${track.track.duration.formatMillis()}`", autoDelete = false) {
                    launch {
                        delay(track.track.duration)
                        it.delete().queue()
                    }
                }

        player.startTrack(track.track, false)
    }

    override fun onTrackEnd(player: AudioPlayer, track: AudioTrack, endReason: AudioTrackEndReason) {
        val wrapped = queue.poll() //Pop from queue
        if(isLooping) queue.add(wrapped)

        next()
    }
}