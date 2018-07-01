package com.perkelle.dev.bot.command.impl.tags

import com.perkelle.dev.bot.command.CommandContext
import com.perkelle.dev.bot.command.Executor
import com.perkelle.dev.bot.datastores.tables.tags.Tags
import com.perkelle.dev.bot.utils.Colors.RED
import com.perkelle.dev.bot.utils.sendEmbed

class TagDeleteCommand: Executor {

    override fun CommandContext.onExecute() {
        if(args.isEmpty()) {
            channel.sendEmbed("Tags", "You need to specify a tag to delete", RED)
            return
        }

        val name = args[0]

        if(!Tags.tagExists(guild.idLong, name)) {
            channel.sendEmbed("Tags", "Invalid tag", RED)
            return
        }

        Tags.removeTag(guild.idLong, name)
        channel.sendEmbed("Tags", "Removed tag `$name`")
    }
}