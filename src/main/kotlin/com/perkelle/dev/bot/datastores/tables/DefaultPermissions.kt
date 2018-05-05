package com.perkelle.dev.bot.datastores.tables

import com.perkelle.dev.bot.command.PermissionList
import com.perkelle.dev.bot.datastores.upsert
import com.perkelle.dev.bot.getConfig
import net.dv8tion.jda.core.entities.Guild
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

object  DefaultPermissions {

    private object Store: Table("${getConfig().getTablePrefix()}defaultpermissions") {
        val guild = long("guild").uniqueIndex().primaryKey()
        val general = Store.bool("general")
        val music = Store.bool("music")
        val musicAdmin = Store.bool("music_admin")
        val moderator = Store.bool("moderator")
        val admin = Store.bool("admin")
    }

    fun setDefaultEveryonePermissions(guild: Guild) {
        transaction {
            Store.upsert(listOf(Store.general, Store.music, Store.musicAdmin, Store.moderator, Store.admin)) {
                it[this.guild] = guild.idLong
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

    fun updateEveryonePermissions(guild: Guild, general: Boolean, music: Boolean, musicAdmin: Boolean, moderator: Boolean, admin: Boolean) {
        transaction {
            Store.upsert(listOf(Store.general, Store.music, Store.musicAdmin, Store.moderator, Store.admin)) {
                it[this.guild] = guild.idLong
                it[this.general] = general
                it[this.music] = music
                it[this.musicAdmin] = musicAdmin
                it[this.moderator] = moderator
                it[this.admin] = admin
            }
        }
    }

    fun getEveryonePermissions(guild: Long): PermissionList {
        return transaction {
            Store.select {
                Store.guild eq guild
            }.map { PermissionList(it[Store.general], it[Store.music], it[Store.musicAdmin], it[Store.moderator], it[Store.admin]) }
                    .firstOrNull() ?: PermissionList(true, true, false, false, false)
        }
    }
}