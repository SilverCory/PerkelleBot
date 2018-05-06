package com.perkelle.dev.bot.command.impl.music

import com.perkelle.dev.bot.command.CommandBuilder
import com.perkelle.dev.bot.command.CommandCategory
import com.perkelle.dev.bot.command.ICommand
import com.perkelle.dev.bot.command.PermissionCategory
import com.perkelle.dev.bot.managers.getWrapper
import com.perkelle.dev.bot.utils.sendEmbed

class PauseCommand: ICommand {

    override fun register() {
        CommandBuilder()
                .setName("pause")
                .setDescription("Toggle music playback")
                .setCategory(CommandCategory.MUSIC)
                .setPermission(PermissionCategory.MUSIC_ADMIN)
                .setExecutor {
                    val newValue = !guild.getWrapper().musicManager.getPlayer().isPaused
                    guild.getWrapper().musicManager.getPlayer().isPaused = newValue

                    if(newValue) channel.sendEmbed("Music", "Paused music playback. Use `p!pause` to resume it again.")
                    else channel.sendEmbed("Music", "Resumed music playback. Use `p!pause` to pause it again.")
                }
    }
}