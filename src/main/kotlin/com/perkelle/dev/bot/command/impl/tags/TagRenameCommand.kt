package com.perkelle.dev.bot.command.impl.tags

import com.perkelle.dev.bot.command.CommandContext
import com.perkelle.dev.bot.command.Executor
import com.perkelle.dev.bot.datastores.tables.Tags
import com.perkelle.dev.bot.utils.Colors.RED
import com.perkelle.dev.bot.utils.sendEmbed

class TagRenameCommand: Executor {

    override fun CommandContext.onExecute() {
        if(args.size < 2) {
            channel.sendEmbed("Tags", "You need to specify a tag to rename and the new name", RED)
            return
        }

        val oldName = args[0]
        val newName = args[1]

        if(!Tags.tagExists(guild.idLong, oldName)) {
            channel.sendEmbed("Tags", "Invalid tag", RED)
            return
        }

        Tags.renameTag(guild.idLong, oldName, newName)
        channel.sendEmbed("Tags", "Renamed tag `$oldName` to `$newName`")
    }
}