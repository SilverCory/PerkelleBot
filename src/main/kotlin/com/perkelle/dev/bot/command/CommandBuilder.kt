package com.perkelle.dev.bot.command

import com.perkelle.dev.bot.listeners.CommandListener

class CommandBuilder {

    lateinit var name: String
    lateinit var description: String
    lateinit var executor: CommandContext.() -> Unit
    var botAdminOnly = false
    val aliases = mutableListOf<String>()
    val children = mutableListOf<CommandBuilder>()

    init {
        CommandListener.commands.add(this)
    }

    fun name(name: String): CommandBuilder {
        this.name = name
        return this
    }

    fun aliases(vararg alias: String): CommandBuilder {
        this.aliases.addAll(alias)
        return this
    }

    fun description(description: String): CommandBuilder {
        this.description = description
        return this
    }

    fun executor(executor: CommandContext.() -> Unit): CommandBuilder {
        this.executor = executor
        return this
    }

    fun botAdminOnly(botAdminOnly: Boolean): CommandBuilder {
        this.botAdminOnly = botAdminOnly
        return this
    }

    fun child(commandBuilder: CommandBuilder): CommandBuilder {
        children.add(commandBuilder)
        return this
    }
}