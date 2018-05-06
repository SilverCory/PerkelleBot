package com.perkelle.dev.bot.command.impl.general

import com.perkelle.dev.bot.command.CommandBuilder
import com.perkelle.dev.bot.command.CommandCategory
import com.perkelle.dev.bot.command.ICommand
import com.perkelle.dev.bot.command.PermissionCategory
import com.perkelle.dev.bot.utils.sendEmbed

class ShardCommand: ICommand {

    override fun register() {
        CommandBuilder()
                .setName("shard")
                .setDescription("Tells you what shard a guild is on")
                .setCategory(CommandCategory.GENERAL)
                .setPermission(PermissionCategory.GENERAL)
                .setExecutor {
                    if(args.size < 2 || args.any { it.toIntOrNull() == null }) channel.sendEmbed("Shard", "This guild is on shard `${guild.jda.shardInfo.shardId}`\nPing to gateway: `${guild.jda.ping}ms`")
                    else channel.sendEmbed("Shard", "That guild is on shard `${(args[0].toInt() shr 22) % args[1].toInt()}`")
                }
    }
}