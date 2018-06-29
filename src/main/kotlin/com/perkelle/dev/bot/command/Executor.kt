package com.perkelle.dev.bot.command

interface Executor {

    fun CommandContext.onExecute()

    fun execute(commandContext: CommandContext) = commandContext.onExecute()
}