package com.perkelle.dev.bot.datastores.tables.permissions

import com.perkelle.dev.bot.command.PermissionList
import com.perkelle.dev.bot.datastores.DataStore
import com.perkelle.dev.bot.datastores.upsert
import com.perkelle.dev.bot.getConfig
import net.dv8tion.jda.core.entities.Role
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

object RolePermissions: DataStore {

    private object Store: Table("${getConfig().getTablePrefix()}rolepermissions") {
        val role = long("role").uniqueIndex().primaryKey()
        val general = bool("general")
        val tickets = bool("tickets").default(false)
        val ticketsManager = bool("tickets_manager").default(false)
        val music = bool("music")
        val musicAdmin = bool("music_admin")
        val moderator = bool("moderator")
        val admin = bool("admin")
    }

    override val instance: Table
        get() = Store

    override fun getTable() = instance

    fun updateRolePermissions(role: Role, general: Boolean, tickets: Boolean, ticketsManager: Boolean, music: Boolean, musicAdmin: Boolean, moderator: Boolean, admin: Boolean) {
        transaction {
            Store.upsert(listOf(Store.general, Store.tickets, Store.ticketsManager, Store.music, Store.musicAdmin, Store.moderator, Store.admin)) {
                it[Store.role] = role.idLong
                it[Store.general] = general
                it[Store.tickets] = tickets
                it[Store.ticketsManager] = ticketsManager
                it[Store.music] = music
                it[Store.musicAdmin] = musicAdmin
                it[Store.moderator] = moderator
                it[Store.admin] = admin
            }
        }
    }

    fun getRolePermissions(role: Role): PermissionList? {
        return transaction {
            Store.select {
                Store.role eq role.idLong
            }.map { PermissionList(it[Store.general], it[Store.tickets], it[Store.ticketsManager], it[Store.music], it[Store.musicAdmin], it[Store.moderator], it[Store.admin]) }.firstOrNull()
        }
    }
}