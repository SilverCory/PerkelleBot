package com.perkelle.dev.bot.command.impl.music

import com.perkelle.dev.bot.command.CommandBuilder
import com.perkelle.dev.bot.command.CommandCategory
import com.perkelle.dev.bot.command.ICommand
import com.perkelle.dev.bot.command.PermissionCategory
import com.perkelle.dev.bot.managers.getWrapper
import com.perkelle.dev.bot.utils.Colors
import com.perkelle.dev.bot.utils.formatMillis
import com.perkelle.dev.bot.utils.sendEmbed
import java.util.concurrent.TimeUnit

class SeekCommand: ICommand {

    private val timePattern = Regex("(?:(?<hours>\\d{1,2}):)?(?:(?<minutes>\\d{1,2}):)?(?<seconds>\\d{1,2})")

    override fun register() {
        CommandBuilder()
                .setName("seek")
                .setDescription("Jump to a timestamp in the song")
                .setAliases("jump", "timestamp")
                .setCategory(CommandCategory.MUSIC)
                .setPermission(PermissionCategory.MUSIC)
                .setExecutor {
                    if(args.isEmpty() || !args[0].matches(timePattern)) {
                        channel.sendEmbed("Seek", "You need to specify a timestamp", Colors.RED)
                        return@setExecutor
                    }

                    val matchResult = timePattern.find(args[0])!!

                    var hours = matchResult.groups["hours"]?.value?.replace(":", "")?.toIntOrNull()
                    var minutes = matchResult.groups["minutes"]?.value?.replace(":", "")?.toIntOrNull()
                    val seconds = matchResult.groups["seconds"]!!.value.replace(":", "").toInt()

                    if(minutes == null && hours != null) {
                        minutes = hours
                        hours = null
                    }

                    if((hours != null && hours < 0) || (minutes != null && (minutes < 0 || minutes > 59)) || (seconds < 0 || seconds > 59)) {
                        channel.sendEmbed("Seek", "Invalid timestamp", Colors.RED)
                        return@setExecutor
                    }

                    var millis = 0L
                    if(hours != null) millis += TimeUnit.HOURS.toMillis(hours.toLong())
                    if(minutes != null) millis += TimeUnit.MINUTES.toMillis(minutes.toLong())
                    millis += TimeUnit.SECONDS.toMillis(seconds.toLong())

                    guild.getWrapper().musicManager.getPlayer().playingTrack.position = millis

                    channel.sendEmbed("Seek", "Jumped to ${millis.formatMillis()}")
                }
    }
}