package com.perkelle.dev.bot.listeners

import com.perkelle.dev.bot.datastores.RedisBackend
import com.perkelle.dev.bot.datastores.tables.tickets.TicketManagers
import com.perkelle.dev.bot.getConfig
import com.perkelle.dev.bot.managers.getWrapper
import com.perkelle.dev.bot.utils.Colors
import com.perkelle.dev.bot.utils.sendEmbed
import kotlinx.coroutines.experimental.launch
import net.dv8tion.jda.core.events.guild.GuildJoinEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter

class JoinListener: ListenerAdapter() {

    override fun onGuildJoin(e: GuildJoinEvent) {
        val guild = e.guild

        launch {
            val ownerHasPremium = guild.getWrapper().isPremium()
            if(getConfig().isPremium() && !ownerHasPremium) {
                guild.owner.user.openPrivateChannel().queue { it.sendEmbed("No Permission", "This bot is for premium users only. Type `p!premium` on the main bot for more information", Colors.RED, autoDelete = false) }
                guild.leave().queue()
            }

            TicketManagers.addManager(guild.idLong, guild.owner.user.idLong)

            val shard = e.jda.shardInfo.shardId
            val count = e.jda.guilds.size
            RedisBackend.ServerCount.setCount(shard, count)
            RedisBackend.ServerCount.broadcastUpdate(shard, count)
        }
    }
}