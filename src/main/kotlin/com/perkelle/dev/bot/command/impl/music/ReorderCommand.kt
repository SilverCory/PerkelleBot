package com.perkelle.dev.bot.command.impl.music

import com.perkelle.dev.bot.command.CommandBuilder
import com.perkelle.dev.bot.command.CommandCategory
import com.perkelle.dev.bot.command.ICommand
import com.perkelle.dev.bot.command.PermissionCategory
import com.perkelle.dev.bot.managers.getWrapper
import com.perkelle.dev.bot.utils.Colors
import com.perkelle.dev.bot.utils.sendEmbed

class ReorderCommand: ICommand {

    override fun register() {
        CommandBuilder()
                .setName("reorder")
                .setDescription("Move a song to a new position in the queue")
                .setCategory(CommandCategory.MUSIC)
                .setPermission(PermissionCategory.MUSIC_ADMIN)
                .setExecutor {
                    if(args.size < 2 || args[0].toIntOrNull() == null || args[1].toIntOrNull() == null) {
                        channel.sendEmbed("Music", "You need to specify the current position of the song and the position to move it to", Colors.RED)
                        return@setExecutor
                    }

                    val mm = guild.getWrapper().musicManager

                    var current = args[0].toInt() - 1
                    var newPosition = args[1].toInt() - 1
                    if(newPosition == mm.getScheduler().getQueue().size) newPosition -= 1
                    if(current == 0) current += 1

                    if(current >= mm.getScheduler().getQueue().size || newPosition > mm.getScheduler().getQueue().size || current == 0) {
                        channel.sendEmbed("Music", "Invalid song position", Colors.RED)
                        return@setExecutor
                    }

                    val track = mm.getScheduler().getQueue().removeAt(current)
                    mm.getScheduler().getQueue().add(newPosition, track)
                    channel.sendEmbed("Music", "Reordered `${track.track.info.title}` to position `${newPosition + 1}`")
                }
    }
}