package com.perkelle.dev.bot.command

import com.perkelle.dev.bot.listeners.CommandListener

class CommandBuilder(isChild: Boolean = false) {

    lateinit var name: String
    lateinit var description: String
    var category = CommandCategory.GENERAL //Default to general
    var permissionCategory = PermissionCategory.GENERAL //Default to general
    lateinit var executor: CommandContext.() -> Unit
    var botAdminOnly = false
    var premiumOnly = false
    val aliases = mutableListOf<String>()
    val children = mutableListOf<CommandBuilder>()

    init {
        if(!isChild) CommandListener.commands.add(this)
    }

    fun setName(name: String): CommandBuilder {
        this.name = name
        return this
    }

    fun setAliases(vararg alias: String): CommandBuilder {
        this.aliases.addAll(alias)
        return this
    }

    fun setDescription(description: String): CommandBuilder {
        this.description = description
        return this
    }

    fun setCategory(category: CommandCategory): CommandBuilder {
        this.category = category
        return this
    }

    fun setPermission(permission: PermissionCategory): CommandBuilder {
        this.permissionCategory = permission
        return this
    }

    fun setExecutor(executor: CommandContext.() -> Unit): CommandBuilder {
        this.executor = executor
        return this
    }

    fun setBotAdminOnly(botAdminOnly: Boolean): CommandBuilder {
        this.botAdminOnly = botAdminOnly
        return this
    }

    fun setPremiumOnly(premiumOnly: Boolean): CommandBuilder {
        this.premiumOnly = premiumOnly
        return this
    }

    fun addChild(commandBuilder: CommandBuilder): CommandBuilder {
        children.add(commandBuilder)
        return this
    }
}