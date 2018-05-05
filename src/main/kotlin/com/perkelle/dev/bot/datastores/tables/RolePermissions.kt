package com.perkelle.dev.bot.datastores.tables

import com.perkelle.dev.bot.command.PermissionList
import com.perkelle.dev.bot.datastores.upsert
import com.perkelle.dev.bot.getConfig
import net.dv8tion.jda.core.entities.Role
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

object RolePermissions {

    private object Store: Table("${getConfig().getTablePrefix()}rolepermissions") {
        val role = long("role").uniqueIndex().primaryKey()
        val general = bool("general")
        val music = bool("music")
        val musicAdmin = bool("music_admin")
        val moderator = bool("moderator")
        val admin = bool("admin")
    }

    fun updateRolePermissions(role: Role, general: Boolean, music: Boolean, musicAdmin: Boolean, moderator: Boolean, admin: Boolean) {
        transaction {
            Store.upsert(listOf(Store.general, Store.music, Store.musicAdmin, Store.moderator, Store.admin)) {
                it[this.role] = role.idLong
                it[this.general] = general
                it[this.music] = music
                it[this.musicAdmin] = musicAdmin
                it[this.moderator] = moderator
                it[this.admin] = admin
            }
        }
    }

    fun getRolePermissions(role: Role): PermissionList? {
        return transaction {
            Store.select {
                Store.role eq role.idLong
            }.map { PermissionList(it[Store.general], it[Store.music], it[Store.musicAdmin], it[Store.moderator], it[Store.admin]) }.firstOrNull()
        }
    }
}