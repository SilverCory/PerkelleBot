package com.perkelle.dev.bot.music

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame
import net.dv8tion.jda.core.audio.AudioSendHandler

class AudioPlayerSendHandler(private val audioPlayer: AudioPlayer) : AudioSendHandler {

    private var lastFrame: AudioFrame? = null

    override fun provide20MsAudio() = lastFrame!!.data!!

    override fun canProvide(): Boolean {
        lastFrame = audioPlayer.provide()
        return lastFrame != null
    }

    override fun isOpus() = true
}