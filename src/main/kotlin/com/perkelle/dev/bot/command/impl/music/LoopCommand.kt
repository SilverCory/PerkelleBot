package com.perkelle.dev.bot.command.impl.music

import com.perkelle.dev.bot.command.CommandBuilder
import com.perkelle.dev.bot.command.CommandCategory
import com.perkelle.dev.bot.command.ICommand
import com.perkelle.dev.bot.command.PermissionCategory
import com.perkelle.dev.bot.managers.getWrapper
import com.perkelle.dev.bot.utils.sendEmbed

class LoopCommand: ICommand {

    override fun register() {
        CommandBuilder()
                .setName("loop")
                .setDescription("Enabled queue looping")
                .setAliases("repeat")
                .setCategory(CommandCategory.MUSIC)
                .setPermission(PermissionCategory.MUSIC_ADMIN)
                .setExecutor {
                    guild.getWrapper().musicManager.getScheduler().looping = !guild.getWrapper().musicManager.getScheduler().looping

                    if(guild.getWrapper().musicManager.getScheduler().looping) channel.sendEmbed("Music", "Enabled queue looping")
                    else channel.sendEmbed("Music", "Disabled queue looping")
                }
    }
}