package com.perkelle.dev.bot.command.impl.music

import com.perkelle.dev.bot.command.CommandBuilder
import com.perkelle.dev.bot.command.CommandCategory
import com.perkelle.dev.bot.command.ICommand
import com.perkelle.dev.bot.command.PermissionCategory
import com.perkelle.dev.bot.managers.getWrapper
import com.perkelle.dev.bot.music.AudioTrackWrapper
import com.perkelle.dev.bot.utils.formatMillis
import com.perkelle.dev.bot.utils.sendEmbed

class QueueCommand: ICommand {

    override fun register() {
        CommandBuilder()
                .setName("queue")
                .setDescription("Shows you a list of all songs in the queue")
                .setAliases("q", "list")
                .setCategory(CommandCategory.MUSIC)
                .setPermission(PermissionCategory.MUSIC)
                .setExecutor {
                    val fullList = guild.getWrapper().musicManager.queue
                    val page =
                            if (args.isEmpty() || args[0].toIntOrNull() == null || args[0].toInt() < 1 || args[0].toInt() > Math.ceil(fullList.size / 5.0)) 0
                            else args[0].toInt() - 1

                    val subList = mutableListOf<AudioTrackWrapper>()
                    for (i in page * 5..page * 5 + 5) {
                        if (i > fullList.size - 1) break
                        else subList.add(fullList.toList()[i])
                    }

                    channel.sendEmbed("Music", "${subList.withIndex().joinToString("\n") { (index, track) -> "`${page * 5 + index + 1}` ${track.track.info.title} `${track.track.duration.formatMillis()}`" }} \n\nPage `${page + 1} / ${Math.ceil(fullList.size / 5.0).toInt()}`")
                }
    }
}
