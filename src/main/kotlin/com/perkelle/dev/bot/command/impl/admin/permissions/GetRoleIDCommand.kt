package com.perkelle.dev.bot.command.impl.admin.permissions

import com.perkelle.dev.bot.command.CommandBuilder
import com.perkelle.dev.bot.command.CommandCategory
import com.perkelle.dev.bot.command.ICommand
import com.perkelle.dev.bot.command.PermissionCategory
import com.perkelle.dev.bot.utils.Colors
import com.perkelle.dev.bot.utils.sendEmbed

class GetRoleIDCommand: ICommand {

    override fun register() {
        CommandBuilder()
                .setName("getroleid")
                .setDescription("Gets the Discord ID of a role by name")
                .setCategory(CommandCategory.SETTINGS)
                .setPermission(PermissionCategory.ADMIN)
                .setExecutor {
                    val role = guild.roles.firstOrNull { args.joinToString(" ").equals(it.name, true) }

                    if(role == null) {
                        channel.sendEmbed("Role ID", "Invalid role name", Colors.RED)
                        return@setExecutor
                    }

                    channel.sendEmbed("Role ID", "ID of `${args.joinToString(" ")}` is `${role.id}`")
                }
    }
}