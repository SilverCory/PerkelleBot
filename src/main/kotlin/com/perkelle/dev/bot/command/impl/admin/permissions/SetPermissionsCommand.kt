package com.perkelle.dev.bot.command.impl.admin.permissions

import com.perkelle.dev.bot.command.*
import com.perkelle.dev.bot.datastores.tables.permissions.DefaultPermissions
import com.perkelle.dev.bot.datastores.tables.permissions.RolePermissions
import com.perkelle.dev.bot.managers.getWrapper
import com.perkelle.dev.bot.utils.Colors
import com.perkelle.dev.bot.utils.boolValue
import com.perkelle.dev.bot.utils.sendEmbed

class SetPermissionsCommand: ICommand {

    override fun register() {
        val cmdBuilder = CommandBuilder()
                .setName("setperms")
                .setDescription("Update the permissions of a role or everyone")
                .setAliases("setpermissions")
                .setPermission(PermissionCategory.ADMIN)
                .setCategory(CommandCategory.SETTINGS)
                .setExecutor {
                    if(args.isEmpty()) {
                        channel.sendEmbed("Permissions", "You need to specify a role name", Colors.RED)
                        return@setExecutor
                    }

                    val roleName = args[0]
                    val role = guild.roles.firstOrNull { it.name.equals(roleName, true) }

                    if(role == null) {
                        channel.sendEmbed("Permissions", "Invalid role", Colors.RED)
                        return@setExecutor
                    }

                    val default = DefaultPermissions.getEveryonePermissions(guild.idLong)
                    val rolePerms = RolePermissions.getRolePermissions(role)

                    val updated = updatePermissionsList(rolePerms ?: default, args.copyOfRange(1, args.size).joinToString(" "))
                    val permsList = updated.first
                    val invalid = updated.second

                    guild.getWrapper().rolePermissions[role] = permsList

                    RolePermissions.updateRolePermissions(role, permsList.general, permsList.tickets, permsList.ticketsManager, permsList.music, permsList.musicAdmin, permsList.moderator, permsList.admin)
                    channel.sendEmbed("Permissions", "Updated permissions for ${role.asMention}")

                    if(invalid.isNotEmpty()) {
                        channel.sendEmbed("Permissions", "You made a syntax error. Visit <https://bot.perkelle.com/permissions.php>, or visit our support guild for help by typing `p!support`", Colors.RED)
                    }
                }

        cmdBuilder.addChild(CommandBuilder(true, cmdBuilder)
                .setName("everyone")
                .setDescription("Update the permissions of everyone")
                .setCategory(CommandCategory.SETTINGS)
                .setPermission(PermissionCategory.ADMIN)
                .setExecutor {
                    if(args.isEmpty()) {
                        channel.sendEmbed("Permissions", "You need to specify permissions. \n**Available permission types:** ${PermissionCategory.values().joinToString(", ") { "`${it.name.toLowerCase()}`" }}. " +
                                "\n**Example syntax:** `p!setperms everyone general=true tickets=false tickets_manager=false music=true music_admin=true moderator=false admin=false`." +
                                "\n**Hint:** You don't have to specify all permissions, only the ones you want to update.")
                        return@setExecutor
                    }

                    val default = DefaultPermissions.getEveryonePermissions(guild.idLong)

                    val updated = updatePermissionsList(default, args.joinToString(" "))
                    val permsList = updated.first
                    val invalid = updated.second

                    guild.getWrapper().defaultPermissions = permsList

                    DefaultPermissions.updateEveryonePermissions(guild, permsList.general, permsList.tickets, permsList.ticketsManager, permsList.music, permsList.musicAdmin, permsList.moderator, permsList.admin)
                    channel.sendEmbed("Permissions", "Updated permissions for everyone")

                    if(invalid.isNotEmpty()) {
                        channel.sendEmbed("Permissions", "You made a syntax error. Type `p!setperms` for an example of valid syntax, or visit our support guild for help by typing `p!support`", Colors.RED)
                    }
                })
    }

    /**
     * @return PermissionsList = updated PermissionsList
     * @return List<String> = Invalid permission categories or no value provided or invalid value
     */
    fun updatePermissionsList(permissionsList: PermissionList, provided: String): Pair<PermissionList, List<String>> {
        val invalid = mutableListOf<String>()
        val subArgs = provided.split(" ")
        val newValues = mutableMapOf<PermissionCategory, Boolean>()

        for(arg in subArgs) {
            val categoryStr = arg.split("=").first().toUpperCase()
            val newValue = arg.split("=").getOrNull(1)?.toLowerCase()
            if(newValue == null || (newValue != "true" && newValue != "false")) {
                invalid.add(categoryStr)
                continue
            }

            if(!PermissionCategory.values().map { it.name }.contains(categoryStr)) {
                invalid.add(categoryStr)
                continue
            }

            val category = PermissionCategory.valueOf(categoryStr)
            newValues[category] = boolValue(newValue)
        }

        return PermissionList(
                newValues[PermissionCategory.GENERAL] ?: permissionsList.general,
                newValues[PermissionCategory.TICKETS] ?: permissionsList.tickets,
                newValues[PermissionCategory.TICKETS_MANAGER] ?: permissionsList.ticketsManager,
                newValues[PermissionCategory.MUSIC] ?: permissionsList.music,
                newValues[PermissionCategory.MUSIC_ADMIN] ?: permissionsList.musicAdmin,
                newValues[PermissionCategory.MODERATOR] ?: permissionsList.moderator,
                newValues[PermissionCategory.ADMIN] ?: permissionsList.admin
                ) to invalid
    }
}