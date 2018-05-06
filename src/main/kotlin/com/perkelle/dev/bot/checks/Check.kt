package com.perkelle.dev.bot.checks

interface Check {

    val regex: Regex

    fun MessageContext.onViolate()

    fun handle(context: MessageContext) = context.onViolate()

    fun isEnabled(guild: Long): Boolean
}