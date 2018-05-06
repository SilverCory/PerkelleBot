package com.perkelle.dev.bot.music

import com.perkelle.dev.bot.datastores.tables.Volume
import com.perkelle.dev.bot.managers.getWrapper
import com.perkelle.dev.bot.utils.formatMillis
import com.perkelle.dev.bot.utils.sendEmbed
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import kotlinx.coroutines.experimental.launch
import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.entities.Member
import net.dv8tion.jda.core.entities.TextChannel
import java.util.*

class TrackScheduler(val player: AudioPlayer, val guildId: Long, val shard: JDA): AudioEventAdapter() {

    private val queue = LinkedList<AudioTrackWrapper>()
    var playing: AudioTrackWrapper? = null
    var looping = false

    var b = ""

    init {
        player.addListener(this)
        getGuild().audioManager.sendingHandler = AudioPlayerSendHandler(player)

        launch {
            player.volume = Volume.getVolume(guildId)
        }
    }

    fun queue(track: AudioTrack, channel: TextChannel, requester: Member) = queue(AudioTrackWrapper(track, channel, requester))

    fun queue(wrapper: AudioTrackWrapper) {
        queue.add(wrapper)
        if(playing == null) next()
    }

    fun getQueue() = queue

    fun next() {
        player.stopTrack()

        if(looping && playing != null) queue.add(AudioTrackWrapper(playing!!.track.makeClone(), playing!!.channel, playing!!.requester))

        if(queue.isEmpty()) {
            playing = null
            getGuild().audioManager.closeAudioConnection()
            return
        }

        val wrapper = queue.pop() ?: return
        playing = wrapper
        b = playing!!.track.info.title

        player.startTrack(wrapper.track, false)
    }

    override fun onTrackStart(player: AudioPlayer, track: AudioTrack) {
        val wrapper = playing ?: return

        wrapper.channel.sendEmbed("Music", "Now playing: **${wrapper.track.info.title}** `${wrapper.track.duration.formatMillis()}`", autoDelete = false) {
            getGuild().getWrapper().nowPlaying?.delete()?.queue()
            getGuild().getWrapper().nowPlaying = it
        }
    }

    override fun onTrackEnd(player: AudioPlayer, track: AudioTrack, endReason: AudioTrackEndReason) {
        if(endReason.mayStartNext) next()
    }

    private fun getGuild() = shard.getGuildById(guildId)
}