package com.perkelle.dev.bot.command.impl.tags

import com.perkelle.dev.bot.command.CommandContext
import com.perkelle.dev.bot.command.Executor
import com.perkelle.dev.bot.datastores.tables.tags.Tags
import com.perkelle.dev.bot.getConfig
import com.perkelle.dev.bot.utils.Colors.RED
import com.perkelle.dev.bot.utils.sendEmbed
import com.perkelle.dev.bot.utils.sendPlain

class TagGetCommand: Executor {

    override fun CommandContext.onExecute() {
        val prefix = getConfig().getDefaultPrefix()

        if(args.isEmpty()) {
            channel.sendEmbed("Tags", "You need to specify a tag (`${prefix}tag list` for a tag list), or `${prefix}tag help` for a list of subcommands", RED)
            return
        }

        val tagName = args[0]
        val tagContents = Tags.getTag(guild.idLong, tagName)
        if(tagContents == null) {
            channel.sendEmbed("Tags", "Invalid tag. Type `${prefix}tag list` for a tag list, or `${prefix}tag help` for a list of subcommands", RED)
            return
        }

        channel.sendPlain(tagContents)
    }
}