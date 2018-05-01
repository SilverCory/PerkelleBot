package com.perkelle.dev.bot.managers

import net.dv8tion.jda.core.entities.Guild

fun Guild.getWrapper(callback: (GuildWrapper) -> Unit = {}) = GuildManager.guilds.computeIfAbsent(idLong, { GuildWrapper(idLong, this.jda, callback) })

object GuildManager {

    val guilds = mutableMapOf<Long, GuildWrapper>()
}