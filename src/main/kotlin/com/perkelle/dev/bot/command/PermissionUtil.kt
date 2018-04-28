package com.perkelle.dev.bot.command

import com.perkelle.dev.bot.getConfig
import com.perkelle.dev.bot.managers.getWrapper
import net.dv8tion.jda.core.entities.Member
import net.dv8tion.jda.core.entities.User

fun Member.hasPermission(category: PermissionCategory): Boolean {
    val wrapper = guild.getWrapper()

    return user.isGlobalAdmin() ||
            isOwner ||
            wrapper.defaultPermissions.isEnabled(category) ||
            roles.any { wrapper.rolePermissions[it]?.isEnabled(category) ?: false }
}

fun User.isGlobalAdmin() = getConfig().getAdminIds().contains(idLong)

fun PermissionList.isEnabled(category: PermissionCategory): Boolean {
    return when(category) {
        PermissionCategory.GENERAL -> general
        PermissionCategory.MUSIC -> music
        PermissionCategory.MUSIC_ADMIN -> musicAdmin
        PermissionCategory.MODERATOR -> moderator
        PermissionCategory.ADMIN -> admin
    }
}

data class PermissionList(val general: Boolean, val music: Boolean, val musicAdmin: Boolean, val moderator: Boolean, val admin: Boolean)