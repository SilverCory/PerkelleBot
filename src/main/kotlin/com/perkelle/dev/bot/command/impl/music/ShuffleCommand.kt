package com.perkelle.dev.bot.command.impl.music

import com.perkelle.dev.bot.command.CommandBuilder
import com.perkelle.dev.bot.command.CommandCategory
import com.perkelle.dev.bot.command.ICommand
import com.perkelle.dev.bot.command.PermissionCategory
import com.perkelle.dev.bot.managers.getWrapper
import com.perkelle.dev.bot.utils.sendEmbed

class ShuffleCommand: ICommand {

    override fun register() {
        CommandBuilder()
                .setName("shuffle")
                .setDescription("Shuffles the queue")
                .setCategory(CommandCategory.MUSIC)
                .setPermission(PermissionCategory.MUSIC_ADMIN)
                .setExecutor {
                    channel.sendEmbed("Music", "Shuffled the queue") //Tell them we shuffled even if its empty

                    val queue = guild.getWrapper().musicManager.getScheduler().getQueue()

                    queue.shuffle()
                }
    }
}