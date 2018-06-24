package com.perkelle.dev.bot.command.impl.admin

import com.perkelle.dev.bot.command.CommandBuilder
import com.perkelle.dev.bot.command.CommandCategory
import com.perkelle.dev.bot.command.ICommand
import com.perkelle.dev.bot.command.PermissionCategory
import com.perkelle.dev.bot.datastores.tables.SelfAssignableRoles
import com.perkelle.dev.bot.utils.Colors
import com.perkelle.dev.bot.utils.sendEmbed

class RevokeSelfAssignCommand: ICommand {

    override fun register() {
        CommandBuilder()
                .setName("revokeselfassign")
                .setDescription("Revoke a role from being self-assignable")
                .setPermission(PermissionCategory.ADMIN)
                .setCategory(CommandCategory.SETTINGS)
                .setExecutor {
                    if(args.isEmpty()) {
                        channel.sendEmbed("Role Assign", "You need to specify a role to remove from the self-assignable roles", Colors.RED)
                        return@setExecutor
                    }

                    val roleName = args.joinToString(" ")
                    val role = guild.roles.firstOrNull { it.name.equals(roleName, true) }

                    if(role == null) {
                        channel.sendEmbed("Role Assign", "Invalid role name", Colors.RED)
                        return@setExecutor
                    }

                    SelfAssignableRoles.disallowSelfAssign(role, guild.idLong)
                    channel.sendEmbed("Role Assign", "`${role.name}` is now self-assignable")
                }
    }
}