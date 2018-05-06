package com.perkelle.dev.bot.command.impl.music

import com.perkelle.dev.bot.command.CommandBuilder
import com.perkelle.dev.bot.command.CommandCategory
import com.perkelle.dev.bot.command.ICommand
import com.perkelle.dev.bot.command.PermissionCategory
import com.perkelle.dev.bot.managers.getWrapper
import com.perkelle.dev.bot.utils.Colors
import com.perkelle.dev.bot.utils.sendEmbed

class RemoveCommand: ICommand {

    override fun register() {
        CommandBuilder()
                .setName("remove")
                .setDescription("Remove a song from the queue at a specified position")
                .setCategory(CommandCategory.MUSIC)
                .setPermission(PermissionCategory.MUSIC_ADMIN)
                .setExecutor {
                    if(args.isEmpty() || args[0].toIntOrNull() == null || args[0].toInt() < 1 || args[0].toInt() > guild.getWrapper().musicManager.getScheduler().getQueue().size) {
                        channel.sendEmbed("Music", "You need to specify a playlist position", Colors.RED)
                        return@setExecutor
                    }

                    val position = args[0].toInt() - 1

                    if(position == 0) guild.getWrapper().musicManager.getPlayer().stopTrack()
                    else guild.getWrapper().musicManager.getScheduler().getQueue().removeAt(position)

                    channel.sendEmbed("Music", "Removed song at position `${position + 1}`")
                }
    }
}