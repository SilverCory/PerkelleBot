package com.perkelle.dev.bot.command.impl.music

import com.perkelle.dev.bot.Constants
import com.perkelle.dev.bot.command.CommandBuilder
import com.perkelle.dev.bot.command.CommandCategory
import com.perkelle.dev.bot.command.ICommand
import com.perkelle.dev.bot.command.PermissionCategory
import com.perkelle.dev.bot.managers.getWrapper
import com.perkelle.dev.bot.utils.Colors
import com.perkelle.dev.bot.utils.sendEmbed

class GainCommand: ICommand {

    override fun register() {
        CommandBuilder()
                .setName("gain")
                .setDescription("Sets the gain level")
                .setPremiumOnly(true)
                .setCategory(CommandCategory.MUSIC)
                .setPermission(PermissionCategory.MUSIC_ADMIN)
                .setExecutor {
                    if(args.isEmpty() || args[0].toIntOrNull() == null || args[0].toInt() > 9 || args[0].toInt() < 0) { //TODO: Make this code nice
                        channel.sendEmbed("Music", "You must specify a gain between 0 and 9", Colors.RED)
                        return@setExecutor
                    }

                    val mm = guild.getWrapper().musicManager

                    if(args[0].toInt() == 0) {
                        mm.getPlayer().setFilterFactory(null)
                        channel.sendEmbed("Music", "Disabled gain")
                        return@setExecutor
                    }

                    val boost = args[0].toFloat() / 10 //Between 1 and 9

                    Constants.BASS_BOOST.withIndex().forEach { (index, value) ->
                        mm.getEqualizer().setGain(index, 0.2f + boost)
                    }

                    mm.getPlayer().setFilterFactory(mm.getEqualizer())

                    channel.sendEmbed("Music", "Set gain level to `${args[0].toInt()}`")
                }
    }
}