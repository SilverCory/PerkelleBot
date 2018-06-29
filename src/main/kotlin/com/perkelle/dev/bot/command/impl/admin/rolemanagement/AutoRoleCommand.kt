package com.perkelle.dev.bot.command.impl.admin.rolemanagement

import com.perkelle.dev.bot.command.CommandBuilder
import com.perkelle.dev.bot.command.CommandCategory
import com.perkelle.dev.bot.command.ICommand
import com.perkelle.dev.bot.command.PermissionCategory
import com.perkelle.dev.bot.datastores.tables.AutoRole
import com.perkelle.dev.bot.utils.Colors.RED
import com.perkelle.dev.bot.utils.sendEmbed
import net.dv8tion.jda.core.Permission

class AutoRoleCommand: ICommand {

    override fun register() {
        CommandBuilder()
                .setName("autorole")
                .setDescription("Enable / disable the automatic assignment of roles on join")
                .setCategory(CommandCategory.SETTINGS)
                .setPermission(PermissionCategory.ADMIN)
                .setExecutor {
                    if(args.isEmpty()) {
                        channel.sendEmbed("Auto Role", "You need to specify a role name or `off`", RED)
                        return@setExecutor
                    }

                    val roleName = args[0].toLowerCase()

                    if(roleName == "off") {
                        AutoRole.disableAutoRoll(guild.idLong)
                        channel.sendEmbed("Auto Role", "Disabled auto role")
                    } else {
                        val role = guild.roles.firstOrNull { it.name.equals(roleName, true) }
                        if(role == null) {
                            channel.sendEmbed("Auto Role", "Invalid role", RED)
                            return@setExecutor
                        }

                        if(!guild.selfMember.hasPermission(Permission.MANAGE_ROLES)) {
                            channel.sendEmbed("Auto Role", "I don't have permission to assign roles to users", RED)
                            return@setExecutor
                        }

                        AutoRole.setRole(guild.idLong, role.idLong)
                        channel.sendEmbed("Auto Role", "Changed auto role to `${role.name}`")
                    }
                }
    }
}