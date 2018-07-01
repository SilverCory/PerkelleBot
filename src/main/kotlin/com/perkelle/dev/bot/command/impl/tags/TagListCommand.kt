package com.perkelle.dev.bot.command.impl.tags

import com.perkelle.dev.bot.command.CommandContext
import com.perkelle.dev.bot.command.Executor
import com.perkelle.dev.bot.datastores.tables.tags.Tags
import com.perkelle.dev.bot.utils.sendEmbed

class TagListCommand: Executor {

    override fun CommandContext.onExecute() {
        val tags = Tags.getTags(guild.idLong).map { it.name }

        channel.sendEmbed("Tags", "Available tags for this guild: ${tags.joinToString(", ") { "`$it`" }}")
    }
}