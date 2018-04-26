package com.perkelle.dev.bot.managers

import net.dv8tion.jda.core.entities.Guild

fun Guild.getWrapper() = GuildManager.guilds.computeIfAbsent(idLong, { GuildWrapper(idLong, this.jda) })

object GuildManager {

    val guilds = mutableMapOf<Long, GuildWrapper>()
}