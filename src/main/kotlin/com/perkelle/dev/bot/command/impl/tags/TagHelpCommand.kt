package com.perkelle.dev.bot.command.impl.tags

import com.perkelle.dev.bot.command.CommandContext
import com.perkelle.dev.bot.command.Executor
import com.perkelle.dev.bot.getConfig
import com.perkelle.dev.bot.utils.sendEmbed

class TagHelpCommand: Executor {

    override fun CommandContext.onExecute() {
        channel.sendEmbed("Tags", "Available commands:\n${commandBuilder.parent!!.children.joinToString("\n") { "`${getConfig().getDefaultPrefix()}tag ${it.name}`" }} ")
    }
}