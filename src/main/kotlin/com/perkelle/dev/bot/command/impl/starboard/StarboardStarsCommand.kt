package com.perkelle.dev.bot.command.impl.starboard

import com.perkelle.dev.bot.command.CommandContext
import com.perkelle.dev.bot.command.Executor
import com.perkelle.dev.bot.datastores.tables.starboard.StarboardAmounts
import com.perkelle.dev.bot.utils.Colors.RED
import com.perkelle.dev.bot.utils.sendEmbed

class StarboardStarsCommand: Executor {

    override fun CommandContext.onExecute() {
        if(args.isEmpty()) {
            channel.sendEmbed("Starboard", "You need to specify an amount of stars", RED)
            return
        }

        val amount = args[0].toIntOrNull()
        if(amount == null) {
            channel.sendEmbed("Starboard", "You need to specify an amount of stars", RED)
            return
        }

        StarboardAmounts.setAmount(guild.idLong, amount)
        channel.sendEmbed("Starboard", "$amount :star:s are required for posts to be added to the starboard")
    }
}