package com.perkelle.dev.bot.command.impl.tags

import com.perkelle.dev.bot.command.CommandContext
import com.perkelle.dev.bot.command.Executor
import com.perkelle.dev.bot.datastores.tables.tags.Tags
import com.perkelle.dev.bot.utils.Colors.RED
import com.perkelle.dev.bot.utils.sendEmbed

class TagEditCommand: Executor {

    override fun CommandContext.onExecute() {
        if (args.size < 2) {
            channel.sendEmbed("Tags", "You need to specify a tag name and new contents", RED)
            return
        }

        val name = args[0]
        val contents = args.copyOfRange(1, args.size).joinToString(" ") // Can't be > 2000 because user can't send messages > 2000

        if (!Tags.tagExists(guild.idLong, name)) {
            channel.sendEmbed("Tags", "Invalid tag", RED)
            return
        }

        Tags.removeTag(guild.idLong, name)
        Tags.createTag(guild.idLong, name, contents)

        channel.sendEmbed("Tags", "Edited contents of tag `$name`")
    }
}