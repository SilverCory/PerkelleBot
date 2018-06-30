package com.perkelle.dev.bot.command.impl.music

import com.perkelle.dev.bot.command.CommandBuilder
import com.perkelle.dev.bot.command.CommandCategory
import com.perkelle.dev.bot.command.ICommand
import com.perkelle.dev.bot.command.PermissionCategory
import com.perkelle.dev.bot.datastores.tables.music.Volume
import com.perkelle.dev.bot.managers.getWrapper
import com.perkelle.dev.bot.utils.Colors
import com.perkelle.dev.bot.utils.sendEmbed

class VolumeCommand: ICommand {

    override fun register() {
        CommandBuilder()
                .setName("volume")
                .setDescription("Change the volume of the bot")
                .setCategory(CommandCategory.MUSIC)
                .setPermission(PermissionCategory.MUSIC_ADMIN)
                .setPremiumOnly(true)
                .setExecutor {
                    if(args.isEmpty() || args[0].toIntOrNull() == null || args[0].toInt() > 150 || args[0].toInt() < 1) { //TODO: Make this code nice
                        channel.sendEmbed("Music", "You must specify a volume between 1 and 150", Colors.RED)
                        return@setExecutor
                    }

                    val volume = args[0].toInt()
                    guild.getWrapper().musicManager.getPlayer().volume = volume
                    Volume.setVolume(guild.idLong, volume)
                    channel.sendEmbed("Music", "Changed volume to `$volume`")
                }
    }
}