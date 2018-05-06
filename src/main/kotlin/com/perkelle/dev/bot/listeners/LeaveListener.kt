package com.perkelle.dev.bot.listeners

import com.perkelle.dev.bot.datastores.RedisBackend
import kotlinx.coroutines.experimental.launch
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter

class LeaveListener: ListenerAdapter() {

    override fun onGuildLeave(e: GuildLeaveEvent) {
        launch {
            val shard = e.jda.shardInfo.shardId
            val count = e.jda.guilds.size
            RedisBackend.ServerCount.setCount(shard, count)
            RedisBackend.ServerCount.broadcastUpdate(shard, count)
        }
    }
}