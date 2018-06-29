package com.perkelle.dev.bot.command.impl.tags

import com.perkelle.dev.bot.command.CommandBuilder
import com.perkelle.dev.bot.command.CommandCategory
import com.perkelle.dev.bot.command.ICommand
import com.perkelle.dev.bot.command.PermissionCategory

class TagCommand: ICommand {

    override fun register() {
        val cmdBuilder = CommandBuilder()
                .setName("tag")
                .setDescription("General command for tags")
                .setCategory(CommandCategory.TAGS)
                .setPermission(PermissionCategory.GENERAL)
                .setExecutor(TagGetCommand())

        cmdBuilder.addChild(
                CommandBuilder(true, cmdBuilder)
                        .setName("list")
                        .setDescription("Get a list of all tags")
                        .setCategory(CommandCategory.TAGS)
                        .setPermission(PermissionCategory.GENERAL)
                        .setExecutor(TagListCommand())
        ).addChild(
                CommandBuilder(true, cmdBuilder)
                        .setName("create")
                        .setDescription("Create a tag")
                        .setCategory(CommandCategory.TAGS)
                        .setPermission(PermissionCategory.ADMIN)
                        .setExecutor(TagCreateCommand())
        ).addChild(
                CommandBuilder(true, cmdBuilder)
                        .setName("delete")
                        .setDescription("Delete a tag")
                        .setCategory(CommandCategory.TAGS)
                        .setPermission(PermissionCategory.ADMIN)
                        .setExecutor(TagDeleteCommand())
        ).addChild(
                CommandBuilder(true, cmdBuilder)
                        .setName("edit")
                        .setDescription("Edit a tag")
                        .setCategory(CommandCategory.TAGS)
                        .setPermission(PermissionCategory.ADMIN)
                        .setExecutor(TagEditCommand())
        ).addChild(
                CommandBuilder(true, cmdBuilder)
                        .setName("rename")
                        .setDescription("Rename a tag")
                        .setCategory(CommandCategory.TAGS)
                        .setPermission(PermissionCategory.ADMIN)
                        .setExecutor(TagRenameCommand())
        ).addChild(
                CommandBuilder(true, cmdBuilder)
                        .setName("help")
                        .setDescription("Shows a list of the tag commands")
                        .setCategory(CommandCategory.TAGS)
                        .setPermission(PermissionCategory.GENERAL)
                        .setExecutor(TagHelpCommand())
        )
    }
}