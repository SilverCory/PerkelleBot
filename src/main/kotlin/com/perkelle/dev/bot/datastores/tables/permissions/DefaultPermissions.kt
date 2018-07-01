package com.perkelle.dev.bot.datastores.tables.permissions

import com.perkelle.dev.bot.command.PermissionList
import com.perkelle.dev.bot.datastores.DataStore
import com.perkelle.dev.bot.datastores.upsert
import com.perkelle.dev.bot.getConfig
import net.dv8tion.jda.core.entities.Guild
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

object  DefaultPermissions: DataStore {

    private object Store: Table("${getConfig().getTablePrefix()}defaultpermissions") {
        val guild = long("guild").uniqueIndex().primaryKey()
        val general = bool("general")
        val tickets = bool("tickets").default(false)
        val ticketsManager = bool("ticketsManager").default(false)
        val music = bool("music")
        val musicAdmin = bool("music_admin")
        val moderator = bool("moderator")
        val admin = bool("admin")
    }

    override val instance: Table
        get() = Store

    override fun getTable() = instance

    fun setDefaultEveryonePermissions(guild: Guild) {
        transaction {
            Store.upsert(listOf(Store.general, Store.music, Store.musicAdmin, Store.moderator, Store.admin)) {
                it[Store.guild] = guild.idLong
                it[general] = true
                it[music] = true
                it[musicAdmin] = false
                it[moderator] = false
                it[admin] = false
            }
        }
    }

    fun hasEveryonePermissions(guild: Guild): Boolean {
        return transaction {
            Store.select {
                Store.guild eq guild.idLong
            }.firstOrNull() != null
        }
    }

    fun updateEveryonePermissions(guild: Guild, general: Boolean, tickets: Boolean, ticketsManager: Boolean, music: Boolean, musicAdmin: Boolean, moderator: Boolean, admin: Boolean) {
        transaction {
            Store.upsert(listOf(Store.general, Store.music, Store.musicAdmin, Store.moderator, Store.admin)) {
                it[Store.guild] = guild.idLong
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

    fun getEveryonePermissions(guild: Long): PermissionList {
        return transaction {
            Store.select {
                Store.guild eq guild
            }.map { PermissionList(it[Store.general], it[Store.tickets], it[Store.ticketsManager], it[Store.music], it[Store.musicAdmin], it[Store.moderator], it[Store.admin]) }
                    .firstOrNull() ?: PermissionList(true, false, false, true, false, false, false)
        }
    }
}