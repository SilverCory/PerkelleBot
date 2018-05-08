package com.perkelle.dev.bot.command.impl.music

import com.perkelle.dev.bot.Constants
import com.perkelle.dev.bot.command.CommandBuilder
import com.perkelle.dev.bot.command.CommandCategory
import com.perkelle.dev.bot.command.ICommand
import com.perkelle.dev.bot.command.PermissionCategory
import com.perkelle.dev.bot.managers.getWrapper
import com.perkelle.dev.bot.utils.Colors
import com.perkelle.dev.bot.utils.sendEmbed

class BassCommand: ICommand {

    override fun register() {
        CommandBuilder()
                .setName("bass")
                .setDescription("Sets the bass-boost level")
                .setPremiumOnly(true)
                .setCategory(CommandCategory.MUSIC)
                .setPermission(PermissionCategory.MUSIC_ADMIN)
                .setExecutor {
                    if(args.isEmpty() || args[0].toIntOrNull() == null || args[0].toInt() > 9 || args[0].toInt() < 0) { //TODO: Make this code nice
                        channel.sendEmbed("Music", "You must specify a bass boost between 0 and 9", Colors.RED)
                        return@setExecutor
                    }

                    val mm = guild.getWrapper().musicManager

                    if(args[0].toInt() == 0) {
                        mm.getPlayer().setFilterFactory(null)
                        channel.sendEmbed("Music", "Disabled bass boost")
                        return@setExecutor
                    }

                    val boost = args[0].toInt() / 10

                    Constants.BASS_BOOST.withIndex().forEach { (index, value) ->
                        mm.getEqualizer().setGain(index, value + boost)
                    }
                    mm.getPlayer().setFilterFactory(mm.getEqualizer())

                    channel.sendEmbed("Music", "Set bass boost level to `$boost`")
                }
    }
}