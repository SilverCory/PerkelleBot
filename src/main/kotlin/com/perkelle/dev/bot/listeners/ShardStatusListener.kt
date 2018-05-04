package com.perkelle.dev.bot.listeners

import com.perkelle.dev.bot.datastores.RedisBackend
import com.perkelle.dev.bot.getConfig
import com.perkelle.dev.bot.managers.GuildManager
import com.perkelle.dev.bot.managers.GuildWrapper
import net.dv8tion.jda.core.events.DisconnectEvent
import net.dv8tion.jda.core.events.ReadyEvent
import net.dv8tion.jda.core.events.StatusChangeEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter

class ShardStatusListener: ListenerAdapter() {

    override fun onReady(e: ReadyEvent) {
        e.jda.guilds.forEach { GuildManager.guilds[it.idLong] = GuildWrapper(it.idLong, e.jda) }

        val shard = e.jda.shardInfo.shardId
        val count = e.jda.guilds.size
        RedisBackend.ServerCount.setCount(shard, count)
        RedisBackend.ServerCount.broadcastUpdate(shard, count)
    }

    override fun onDisconnect(e: DisconnectEvent) {
        e.jda.guilds.map { it.idLong }.forEach { GuildManager.guilds.remove(it) }
    }

    override fun onStatusChange(e: StatusChangeEvent) {
        val id = e.jda.shardInfo?.shardId ?: return
        val status = e.newStatus.name

        if(!getConfig().isPremium()) {
            RedisBackend.ShardStatus.setStatus(id, status)
            RedisBackend.ShardStatus.broadcastUpdate(id, status)
        }
    }
}