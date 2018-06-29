package com.perkelle.dev.bot.command.impl.admin.rolemanagement

import com.perkelle.dev.bot.command.CommandBuilder
import com.perkelle.dev.bot.command.CommandCategory
import com.perkelle.dev.bot.command.ICommand
import com.perkelle.dev.bot.command.PermissionCategory
import com.perkelle.dev.bot.datastores.tables.SelfAssignableRoles
import com.perkelle.dev.bot.utils.Colors
import com.perkelle.dev.bot.utils.sendEmbed

class AllowSelfAssignCommand: ICommand {

    override fun register() {
        CommandBuilder()
                .setName("allowselfassign")
                .setDescription("Allow a role to be self-assignable")
                .setPermission(PermissionCategory.ADMIN)
                .setCategory(CommandCategory.SETTINGS)
                .setExecutor {
                    if(args.isEmpty()) {
                        channel.sendEmbed("Role Assign", "You need to specify a role to set to be self-assignable", Colors.RED)
                        return@setExecutor
                    }

                    val roleName = args.joinToString(" ")
                    val role = guild.roles.firstOrNull { it.name.equals(roleName, true) }

                    if(role == null) {
                        channel.sendEmbed("Role Assign", "Invalid role name", Colors.RED)
                        return@setExecutor
                    }

                    SelfAssignableRoles.allowSelfAssign(role, guild.idLong)
                    channel.sendEmbed("Role Assign", "`${role.name}` is now self-assignable")
                }
    }
}