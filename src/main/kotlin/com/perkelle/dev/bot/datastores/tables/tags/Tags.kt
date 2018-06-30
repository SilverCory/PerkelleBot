package com.perkelle.dev.bot.datastores.tables.tags

import com.perkelle.dev.bot.command.impl.tags.Tag
import com.perkelle.dev.bot.datastores.DataStore
import com.perkelle.dev.bot.utils.with
import com.perkelle.dev.bot.utils.without
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object Tags: DataStore {

    private object Store: Table("tags") {
        val guild = long("guild")
        val name = varchar("name", 100)
        val content = text("content")
    }

    override val instance: Table
        get() = Store

    override fun getTable() = instance

    private val cache = mutableMapOf<Long, List<Tag>>() // Guild ID -> List of Tags

    fun getTag(guild: Long, name: String): String? {
        return if(cache[guild] == null) {
            cache[guild] = getTags(guild)
            getTag(guild, name)
        } else {
            cache[guild]!!.firstOrNull { it.name == name.toLowerCase() }?.contents
        }
    }

    fun createTag(guild: Long, name: String, content: String) {
        val tag = Tag(name.toLowerCase(), content)
        cache[guild] = (cache[guild]?.with(tag)) ?: listOf(tag)

        transaction {
            Store.insert {
                it[Store.guild] = guild
                it[Store.name] = name.toLowerCase()
                it[Store.content] = content
            }
        }
    }

    fun removeTag(guild: Long, name: String) {
        val tag = cache[guild]?.firstOrNull { it.name == name.toLowerCase()}
        if(tag != null) cache[guild] = cache[guild]?.without(tag) ?: listOf()

        transaction {
            Store.deleteWhere {
                (Store.guild eq guild) and (Store.name eq name.toLowerCase())
            }
        }
    }

    fun getTags(guild: Long): List<Tag> {
        val cached = cache[guild]

        return if(cached != null) cached
        else {
            val tags = transaction {
                Store.select {
                    Store.guild eq guild
                }.map { Tag(it[Store.name], it[Store.content]) }
            }

            cache[guild] = tags
            tags
        }
    }

    fun tagExists(guild: Long, name: String): Boolean {
        return if(cache[guild] == null) {
            cache[guild] = getTags(guild)
            tagExists(guild, name)
        } else {
            cache[guild]!!.any { it.name == name.toLowerCase() }
        }
    }

    fun renameTag(guild: Long, oldName: String, newName: String) {
        if(cache[guild] == null) {
            cache[guild] = getTags(guild)
        }

        val oldTag = cache[guild]!!.first { it.name == oldName.toLowerCase() }
        cache[guild] = cache[guild]!!.without(oldTag).with(Tag(newName.toLowerCase(), oldTag.contents))

        transaction {
            Store.update({ (Store.guild eq guild) and (Store.name eq oldName.toLowerCase()) }) {
                it[name] = newName
            }
        }
    }
}