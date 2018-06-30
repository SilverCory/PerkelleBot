package com.perkelle.dev.bot.command.impl.starboard

import com.perkelle.dev.bot.command.CommandBuilder
import com.perkelle.dev.bot.command.CommandCategory
import com.perkelle.dev.bot.command.ICommand
import com.perkelle.dev.bot.command.PermissionCategory

class StarboardCommand: ICommand {

    override fun register() {
        val cmdBuilder = CommandBuilder()
                .setName("starboard")
                .setDescription("Starboard management command")
                .setAliases("sb")
                .setCategory(CommandCategory.STARBOARD)
                .setPermission(PermissionCategory.ADMIN)
                .setExecutor(StarboardHelpCommand())

        cmdBuilder.addChild(
                CommandBuilder(true, cmdBuilder)
                        .setName("off")
                        .setDescription("Disable the starboard module")
                        .setAliases("disable")
                        .setCategory(CommandCategory.STARBOARD)
                        .setPermission(PermissionCategory.ADMIN)
                        .setExecutor(StarboardOffCommand())
        ).addChild(
                CommandBuilder(true, cmdBuilder)
                        .setName("setchannel")
                        .setDescription("Sets the starboard channel to the current channel")
                        .setCategory(CommandCategory.STARBOARD)
                        .setPermission(PermissionCategory.ADMIN)
                        .setExecutor(StarboardSetChannelCommand())
        ).addChild(
                CommandBuilder(true, cmdBuilder)
                        .setName("stars")
                        .setDescription("Set the stars required to add the post to the starboard")
                        .setAliases("amount")
                        .setCategory(CommandCategory.STARBOARD)
                        .setPermission(PermissionCategory.ADMIN)
                        .setExecutor(StarboardStarsCommand())
        )
    }
}