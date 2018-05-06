package com.perkelle.dev.bot.music

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import net.dv8tion.jda.core.entities.Member

abstract class GuildAudioController {

    abstract fun getPlayerManager(): AudioPlayerManager
    abstract fun getPlayer(): AudioPlayer
    abstract fun getScheduler(): TrackScheduler

    abstract val voteSkips: MutableList<Member>
}