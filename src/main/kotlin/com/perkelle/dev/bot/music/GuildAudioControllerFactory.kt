package com.perkelle.dev.bot.music

import com.perkelle.dev.bot.getBot
import net.dv8tion.jda.core.entities.Guild
import net.dv8tion.jda.core.entities.Member

object GuildAudioControllerFactory {

    fun createController(guild: Guild): GuildAudioController {
        return object: GuildAudioController() {

            private val player = getPlayerManager().createPlayer()
            private val scheduler = TrackScheduler(getPlayer(), guild.idLong, guild.jda)

            override val voteSkips = mutableListOf<Member>()

            override fun getPlayer() = player

            override fun getPlayerManager() = getBot().playerManager

            override fun getScheduler() = scheduler
        }
    }
}