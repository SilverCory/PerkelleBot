package com.perkelle.dev.bot.checks

import com.perkelle.dev.bot.command.CommandContext

interface Check {

    val regex: Regex

    fun CommandContext.onViolate()
}