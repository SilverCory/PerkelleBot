package com.perkelle.dev.bot.command.datastores

import com.perkelle.dev.bot.command.PermissionList
import com.perkelle.dev.bot.getConfig
import com.perkelle.dev.bot.utils.onComplete
import com.perkelle.dev.bot.utils.onCompleteOrNull
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import net.dv8tion.jda.core.entities.Guild
import net.dv8tion.jda.core.entities.Role
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import javax.sql.DataSource

fun getSQLBackend() = SQLBackend.instance

class SQLBackend {

    companion object {
        lateinit var instance: SQLBackend
    }

    init {
        instance = this
    }

    private lateinit var ds: DataSource

    object Prefixes: Table() {
        val guild = long("guild").uniqueIndex().primaryKey()
        val prefix = varchar("prefix", 4)
    }

    object RolePermissions: Table() {
        val role = long("role").uniqueIndex().primaryKey()
        val general = bool("general")
        val music = bool("music")
        val musicAdmin = bool("music_admin")
        val moderator = bool("moderator")
        val admin = bool("admin")
    }

    object DefaultPermissions: Table() {
        val guild = long("guild").uniqueIndex().primaryKey()
        val general = DefaultPermissions.bool("general")
        val music = DefaultPermissions.bool("music")
        val musicAdmin = DefaultPermissions.bool("music_admin")
        val moderator = DefaultPermissions.bool("moderator")
        val admin = DefaultPermissions.bool("admin")
    }

    object Volumes: Table() {
        val guild = long("guild").uniqueIndex().primaryKey()
        val volume = integer("volume")
    }

    object DisabledChannels: Table() {
        val channel = long("channel").uniqueIndex().primaryKey()
    }

    fun setup() {
        val config = HikariConfig()
        config.jdbcUrl = "jdbc:mysql://${getConfig().getValue("mysql.host", "localhost")}:3306/${getConfig().getValue("mysql.database", "perkellebot")}"
        config.username = getConfig().getValue("mysql.username", "root")
        config.password = getConfig().getValue("mysql.password", "")
        config.addDataSourceProperty("autoReconnect", true)
        config.addDataSourceProperty("useJDBCCompliantTimezoneShift", true)
        config.addDataSourceProperty("serverTimezone", "UTC")
        config.addDataSourceProperty("useLegacyDatetimeCode", false)
        config.addDataSourceProperty("cachePrepStmts", true)
        config.addDataSourceProperty("prepStmtCacheSize", 250)
        config.addDataSourceProperty("prepStmtCacheSqlLimit", 2048)
        config.addDataSourceProperty("maxIdle", 1)
        config.maximumPoolSize = 5

        ds = HikariDataSource(config)

        Database.connect(ds)

        launch {
            transaction {
                SchemaUtils.create(Prefixes)
                SchemaUtils.create(DefaultPermissions)
                SchemaUtils.create(RolePermissions)
                SchemaUtils.create(DisabledChannels)
                SchemaUtils.create(Volumes)
            }
        }
    }

    fun isDisabled(channelId: Long, callback: (Boolean) -> Unit) {
        async {
            return@async transaction {
                DisabledChannels.select {
                    DisabledChannels.channel eq channelId
                }.count() == 1
            }
        }.onComplete(callback)
    }

    fun setDisabled(channelId: Long, isDisabled: Boolean) {
        launch {
            if(isDisabled) {
                transaction {
                    DisabledChannels.insert {
                        it[channel] = channelId
                    }
                }
            } else {
                transaction {
                    DisabledChannels.deleteWhere {
                        DisabledChannels.channel eq channelId
                    }
                }
            }
        }
    }

    fun setDefaultEveryonePermissions(guild: Guild) {
        launch {
            transaction {
                DefaultPermissions.upsert(listOf(DefaultPermissions.general, DefaultPermissions.music, DefaultPermissions.musicAdmin, DefaultPermissions.moderator, DefaultPermissions.admin)) {
                    it[this.guild] = guild.idLong
                    it[general] = true
                    it[music] = true
                    it[musicAdmin] = false
                    it[moderator] = false
                    it[admin] = false
                }
            }
        }
    }

    fun hasEveryonePermissions(guild: Guild, callback: (Boolean) -> Unit) {
        async {
            return@async transaction {
                DefaultPermissions.select {
                    DefaultPermissions.guild eq guild.idLong
                }.firstOrNull() != null
            }
        }.onComplete(callback)
    }

    fun updateEveryonePermissions(guild: Guild, general: Boolean, music: Boolean, musicAdmin: Boolean, moderator: Boolean, admin: Boolean) {
        launch {
            transaction {
                DefaultPermissions.upsert(listOf(DefaultPermissions.general, DefaultPermissions.music, DefaultPermissions.musicAdmin, DefaultPermissions.moderator, DefaultPermissions.admin)) {
                    it[this.guild] = guild.idLong
                    it[this.general] = general
                    it[this.music] = music
                    it[this.musicAdmin] = musicAdmin
                    it[this.moderator] = moderator
                    it[this.admin] = admin
                }
            }
        }
    }

    fun getEveryonePermissions(guild: Long, callback: (PermissionList) -> Unit) {
        async {
            return@async transaction {
                return@transaction DefaultPermissions.select {
                    DefaultPermissions.guild eq guild
                }.map { PermissionList(it[DefaultPermissions.general], it[DefaultPermissions.music], it[DefaultPermissions.musicAdmin], it[DefaultPermissions.moderator], it[DefaultPermissions.admin]) }.firstOrNull() ?: PermissionList(true, true, false, false, false)
            }
        }.onComplete(callback)
    }

    fun updateRolePermissions(role: Role, general: Boolean, music: Boolean, musicAdmin: Boolean, moderator: Boolean, admin: Boolean) {
        launch {
            transaction {
                RolePermissions.upsert(listOf(RolePermissions.general, RolePermissions.music, RolePermissions.musicAdmin, RolePermissions.moderator, RolePermissions.admin)) {
                    it[this.role] = role.idLong
                    it[this.general] = general
                    it[this.music] = music
                    it[this.musicAdmin] = musicAdmin
                    it[this.moderator] = moderator
                    it[this.admin] = admin
                }
            }
        }
    }

    fun getRolePermissions(role: Role, callback: (PermissionList?) -> Unit) {
        async {
            transaction {
                RolePermissions.select {
                    RolePermissions.role eq role.idLong
                }.map { PermissionList(it[RolePermissions.general], it[RolePermissions.music], it[RolePermissions.musicAdmin], it[RolePermissions.moderator], it[RolePermissions.admin]) }.firstOrNull()
            }
        }.onCompleteOrNull(callback)
    }

    fun setVolume(guildId: Long, volume: Int) {
        launch {
            transaction {
                Volumes.upsert(listOf(Volumes.volume)) {
                    it[guild] = guildId
                    it[this.volume] = volume
                }
            }
        }
    }

    fun getVolume(guildId: Long, callback: (Int) -> Unit) {
        async {
            return@async transaction {
                return@transaction Volumes.select {
                    Volumes.guild eq guildId
                }.map { it[Volumes.volume] }.firstOrNull() ?: 100
            }
        }.onComplete(callback)
    }

    fun setPrefix(guildId: Long, newPrefix: String) {
        launch {
            transaction {
                Prefixes.upsert(listOf(Prefixes.prefix)) {
                    it[guild] = guildId
                    it[prefix] = newPrefix
                }
            }
        }
    }

    fun getPrefix(guildId: Long, callback: (String?) -> Unit) {
        async {
            transaction {
                Prefixes.select {
                    Prefixes.guild eq guildId
                }.map { it[Prefixes.prefix] }.firstOrNull()
            }
        }.onCompleteOrNull(callback)
    }
}

//Support upserts
fun <T:Table> T.upsert(uniqueColumns: List<Column<*>>, body: T.(UpsertStatement<Number>) -> Unit): UpsertStatement<Number> = UpsertStatement<Number>(this, uniqueColumns).apply {
    body(this)
    execute(TransactionManager.current())
}

class UpsertStatement<Key: Any>(table: Table, val onDupUpdate: List<Column<*>>): InsertStatement<Key>(table, false) {
    override fun prepareSQL(transaction: Transaction): String {
        val onUpdateSQL = if(onDupUpdate.isNotEmpty()) {
            " ON DUPLICATE KEY UPDATE " + onDupUpdate.joinToString { "${transaction.identity(it)}=VALUES(${transaction.identity(it)})" }
        } else ""
        return super.prepareSQL(transaction) + onUpdateSQL
    }
}