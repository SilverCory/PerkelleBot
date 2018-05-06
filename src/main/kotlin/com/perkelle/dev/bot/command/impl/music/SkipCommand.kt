package com.perkelle.dev.bot.command.impl.music

import com.perkelle.dev.bot.command.CommandBuilder
import com.perkelle.dev.bot.command.CommandCategory
import com.perkelle.dev.bot.command.ICommand
import com.perkelle.dev.bot.command.PermissionCategory
import com.perkelle.dev.bot.managers.getWrapper
import com.perkelle.dev.bot.utils.sendEmbed

class SkipCommand: ICommand {

    override fun register() {
        CommandBuilder()
                .setName("skip")
                .setDescription("Skips the current song")
                .setCategory(CommandCategory.MUSIC)
                .setPermission(PermissionCategory.MUSIC_ADMIN)
                .setAliases("s")
                .setExecutor {
                    guild.getWrapper().musicManager.getScheduler().next()
                    channel.sendEmbed("Music", "${sender.asMention} skipped the song")
                }
    }
}