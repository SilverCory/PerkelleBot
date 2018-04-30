package com.perkelle.dev.bot.command.impl.music

import com.perkelle.dev.bot.command.CommandBuilder
import com.perkelle.dev.bot.command.CommandCategory
import com.perkelle.dev.bot.command.ICommand
import com.perkelle.dev.bot.command.PermissionCategory
import com.perkelle.dev.bot.managers.getWrapper
import com.perkelle.dev.bot.utils.sendEmbed

class StopCommand: ICommand {

    override fun register() {
        CommandBuilder()
                .setName("stop")
                .setDescription("Stops all music")
                .setAliases("reset")
                .setCategory(CommandCategory.MUSIC)
                .setPermission(PermissionCategory.MUSIC_ADMIN)
                .setExecutor {
                    val mm = guild.getWrapper().musicManager
                    mm.queue.clear()
                    mm.next()

                    channel.sendEmbed("Music", "Stopped all music")
                }
    }
}