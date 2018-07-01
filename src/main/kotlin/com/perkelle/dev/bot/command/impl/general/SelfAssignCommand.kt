package com.perkelle.dev.bot.command.impl.general

import com.perkelle.dev.bot.command.CommandBuilder
import com.perkelle.dev.bot.command.CommandCategory
import com.perkelle.dev.bot.command.ICommand
import com.perkelle.dev.bot.command.PermissionCategory
import com.perkelle.dev.bot.datastores.tables.roles.SelfAssignableRoles
import com.perkelle.dev.bot.utils.Colors.RED
import com.perkelle.dev.bot.utils.sendEmbed
import net.dv8tion.jda.core.Permission

class SelfAssignCommand: ICommand {

    override fun register() {
        CommandBuilder()
                .setName("selfassign")
                .setAliases("iam")
                .setDescription("Add a self-assignable role to yourself")
                .setCategory(CommandCategory.GENERAL)
                .setPermission(PermissionCategory.GENERAL)
                .setExecutor {
                    val selfAssignableRoles = SelfAssignableRoles.getSelfAssignableRoles(guild.idLong)

                    if(args.isEmpty()) {
                        channel.sendEmbed("Role Assign", "You need to specify a role to assign. This guild's self-assignable roles are: ```${selfAssignableRoles.map { it.name }}```", RED)
                        return@setExecutor
                    }

                    val role = selfAssignableRoles.firstOrNull { it.name.equals(args.joinToString(" "), true) }

                    if(role == null) {
                        channel.sendEmbed("Role Assign", "Invalid role", RED)
                        return@setExecutor
                    }

                    if(!guild.selfMember.hasPermission(Permission.MANAGE_ROLES)) {
                        channel.sendEmbed("Role Assign", "I don't have permission to assign roles.", RED)
                        return@setExecutor
                    }

                    if(!guild.selfMember.canInteract(sender)) {
                        channel.sendEmbed("Role Assign", "I cannot assign roles to users that are superior to me", RED)
                        return@setExecutor
                    }

                    guild.controller.addRolesToMember(sender, role).queue()
                    channel.sendEmbed("Role Assign", "Assigned `${role.name}` to ${sender.asMention}")
                }


        CommandBuilder()
                .setName("selfrevoke")
                .setAliases("iamn", "removerole")
                .setDescription("Remove a self-assignable role from yourself")
                .setCategory(CommandCategory.GENERAL)
                .setPermission(PermissionCategory.GENERAL)
                .setExecutor {
                    val selfAssignableRoles = SelfAssignableRoles.getSelfAssignableRoles(guild.idLong)

                    if(args.isEmpty()) {
                        channel.sendEmbed("Role Assign", "You need to specify a role to revoke. This guild's self-assignable roles are: ```${selfAssignableRoles.joinToString(", ") { it.name }}```", RED)
                        return@setExecutor
                    }

                    val role = selfAssignableRoles.firstOrNull { it.name.equals(args.joinToString(" "), true) }

                    if(role == null) {
                        channel.sendEmbed("Role Assign", "Invalid role", RED)
                        return@setExecutor
                    }

                    if(!guild.selfMember.hasPermission(Permission.MANAGE_ROLES)) {
                        channel.sendEmbed("Role Assign", "I don't have permission to revoke roles.", RED)
                        return@setExecutor
                    }

                    if(!guild.selfMember.canInteract(sender)) {
                        channel.sendEmbed("Role Assign", "I cannot revoke roles to users that are superior to me", RED)
                        return@setExecutor
                    }

                    guild.controller.removeRolesFromMember(sender, role).queue()
                    channel.sendEmbed("Role Assign", "Revoked `${role.name}` from ${sender.asMention}")
                }
    }
}