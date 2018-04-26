package com.perkelle.dev.bot.music

import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import net.dv8tion.jda.core.entities.Member
import net.dv8tion.jda.core.entities.MessageChannel

data class AudioTrackWrapper(val track: AudioTrack, val channel: MessageChannel, val requester: Member)