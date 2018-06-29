package com.perkelle.dev.bot.command.impl.admin.permissions

import com.perkelle.dev.bot.command.*
import com.perkelle.dev.bot.managers.getWrapper
import com.perkelle.dev.bot.utils.Colors
import com.perkelle.dev.bot.utils.sendEmbed

class ViewPermissionsCommand: ICommand {

    override fun register() {
        val cmdBuilder = CommandBuilder()
                .setName("roleperms")
                .setAliases("viewperms", "viewpermissions", "viewroleperms")
                .setDescription("View the permissions that a role (or everyone) has")
                .setCategory(CommandCategory.SETTINGS)
                .setPermission(PermissionCategory.ADMIN)
                .setExecutor {
                    if(message.mentionedRoles.isEmpty()) {
                        channel.sendEmbed("Permissions", "You need to tag a role or specify `everyone`", Colors.RED)
                        return@setExecutor
                    }

                    val wrapper = guild.getWrapper()
                    val role = message.mentionedRoles[0]

                    val perms = wrapper.rolePermissions[role] ?: PermissionList(true, true, false, false, false)
                    channel.sendEmbed("Permissions", """All users have the following permissions:
                                |General: ${perms.general}
                                |Music: ${perms.music}
                                |Music Admin: ${perms.musicAdmin}
                                |Moderator: ${perms.moderator}
                                |Admin: ${perms.admin}
                            """.trimMargin())
                }

        cmdBuilder.addChild(CommandBuilder(true, cmdBuilder)
                .setName("everyone")
                .setDescription("View the permissions of everyone")
                .setAliases("all")
                .setCategory(CommandCategory.SETTINGS)
                .setPermission(PermissionCategory.ADMIN)
                .setExecutor {
                    val perms = guild.getWrapper().defaultPermissions
                    channel.sendEmbed("Permissions", """All users have the following permissions:
                    |General: ${perms.general}
                    |Music: ${perms.music}
                    |Music Admin: ${perms.musicAdmin}
                    |Moderator: ${perms.moderator}
                    |Admin: ${perms.admin}
                    """.trimMargin())
                })
    }
}