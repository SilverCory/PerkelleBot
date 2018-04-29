package com.perkelle.dev.bot.command.impl.admin

import com.perkelle.dev.bot.command.*
import com.perkelle.dev.bot.datastores.getSQLBackend
import com.perkelle.dev.bot.managers.getWrapper
import com.perkelle.dev.bot.utils.Colors
import com.perkelle.dev.bot.utils.boolValue
import com.perkelle.dev.bot.utils.sendEmbed

class SetPermissionsCommand: ICommand {

    override fun register() {
        CommandBuilder()
                .setName("setperms")
                .setDescription("Update the permissions of a role or everyone")
                .setAliases("setpermissions")
                .setPermission(PermissionCategory.ADMIN)
                .setCategory(CommandCategory.SETTINGS)
                .setExecutor {
                    if(message.mentionedRoles.isEmpty() || !args[0].startsWith("<@&", true) || args.size < 2) {
                        channel.sendEmbed("Permissions", "You need to specify permissions. \n**Available permission types:** ${PermissionCategory.values().joinToString(", ") { "`${it.name.toLowerCase()}`" }}. " +
                                "\n**Example syntax:** `p!setperms @DJ general=true music=true music_admin=true moderator=false admin=false`." +
                                "\n**Hint:** You don't have to specify all permissions, only the ones you want to update.", Colors.RED)
                        return@setExecutor
                    }

                    val role = message.mentionedRoles[0]

                    getSQLBackend().getEveryonePermissions(guild.idLong) { default ->
                        getSQLBackend().getRolePermissions(role) {
                            val updated = updatePermissionsList(it ?: default, args.copyOfRange(1, args.size).joinToString(" "))
                            val permsList = updated.first
                            val invalid = updated.second

                            guild.getWrapper().rolePermissions[role] = permsList

                            getSQLBackend().updateRolePermissions(role, permsList.general, permsList.music, permsList.musicAdmin, permsList.moderator, permsList.admin)
                            channel.sendEmbed("Permissions", "Updated permissions for ${role.asMention}")

                            if(invalid.isNotEmpty()) {
                                channel.sendEmbed("Permissions", "You made a syntax error. Type `p!setperms` for an example of valid syntax, or visit our support guild for help by typing `p!support`", Colors.RED)
                            }
                        }
                    }
                }
                .addChild(CommandBuilder(true)
                        .setName("everyone")
                        .setDescription("Update the permissions of everyone")
                        .setCategory(CommandCategory.SETTINGS)
                        .setPermission(PermissionCategory.ADMIN)
                        .setExecutor {
                            if(args.isEmpty()) {
                                channel.sendEmbed("Permissions", "You need to specify permissions. \n**Available permission types:** ${PermissionCategory.values().joinToString(", ") { "`${it.name.toLowerCase()}`" }}. " +
                                        "\n**Example syntax:** `p!setperms everyone general=true music=true music_admin=true moderator=false admin=false`." +
                                        "\n**Hint:** You don't have to specify all permissions, only the ones you want to update.")
                                return@setExecutor
                            }

                            getSQLBackend().getEveryonePermissions(guild.idLong) {
                                val updated = updatePermissionsList(it, args.joinToString(" "))
                                val permsList = updated.first
                                val invalid = updated.second

                                guild.getWrapper().defaultPermissions = permsList

                                getSQLBackend().updateEveryonePermissions(guild, permsList.general, permsList.music, permsList.musicAdmin, permsList.moderator, permsList.admin)
                                channel.sendEmbed("Permissions", "Updated permissions for everyone")

                                if(invalid.isNotEmpty()) {
                                    channel.sendEmbed("Permissions", "You made a syntax error. Type `p!setperms` for an example of valid syntax, or visit our support guild for help by typing `p!support`", Colors.RED)
                                }
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
                newValues[PermissionCategory.MUSIC] ?: permissionsList.music,
                newValues[PermissionCategory.MUSIC_ADMIN] ?: permissionsList.musicAdmin,
                newValues[PermissionCategory.MODERATOR] ?: permissionsList.moderator,
                newValues[PermissionCategory.ADMIN] ?: permissionsList.admin
                ) to invalid
    }
}