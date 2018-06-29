package com.perkelle.dev.bot.command.impl.tags

import com.perkelle.dev.bot.command.CommandContext
import com.perkelle.dev.bot.command.Executor
import com.perkelle.dev.bot.datastores.tables.Tags
import com.perkelle.dev.bot.utils.Colors.RED
import com.perkelle.dev.bot.utils.sendEmbed

class TagCreateCommand: Executor {

    override fun CommandContext.onExecute() {
        if(args.size < 2) {
            channel.sendEmbed("Tags", "You need to specify a tag name and tag contents", RED)
            return
        }

        val name = args[0]
        val contents = args.copyOfRange(1, args.size).joinToString(" ") // Can't be > 2000 because user can't send messages > 2000

        if(name.length > 100) {
            channel.sendEmbed("Tags", "Tag names are limited to 100 characters", RED)
            return
        }

        Tags.createTag(guild.idLong, name, contents)
        channel.sendEmbed("Tags", "Tag `$name` created successfully")
    }
}