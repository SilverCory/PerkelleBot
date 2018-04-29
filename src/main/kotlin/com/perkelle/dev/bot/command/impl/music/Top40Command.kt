package com.perkelle.dev.bot.command.impl.music

import com.perkelle.dev.bot.command.CommandBuilder
import com.perkelle.dev.bot.command.CommandCategory
import com.perkelle.dev.bot.command.ICommand
import com.perkelle.dev.bot.command.PermissionCategory
import com.perkelle.dev.bot.managers.getWrapper
import com.perkelle.dev.bot.music.AudioTrackWrapper
import com.perkelle.dev.bot.utils.sendEmbed

class Top40Command: ICommand {

    override fun register() {
        CommandBuilder()
                .setName("top40")
                .setDescription("Queues the UK top 40 songs")
                .setCategory(CommandCategory.MUSIC)
                .setPermission(PermissionCategory.MUSIC_ADMIN)
                .setPremiumOnly(true)
                .setExecutor {
                    val mm = guild.getWrapper().musicManager
                    mm.queue.clear()
                    mm.next()
                    val tracks = mm.loadTracks("https://www.youtube.com/playlist?list=PLx0sYbCqOb8Q_CLZC2BdBSKEEB59BOPUM").first
                    tracks.forEach { guild.getWrapper().musicManager.queue(AudioTrackWrapper(it, channel, sender), true) }
                    channel.sendEmbed("Music", "Queued the UK top 40")
                }
    }
}