package com.perkelle.dev.bot.checks.impl

import com.perkelle.dev.bot.checks.Check
import com.perkelle.dev.bot.command.CommandContext

class InviteCheck: Check {

    override val regex = Regex("(?:http|https)://discord\\.gg/\\w*")

    override fun CommandContext.onViolate() {
        message.delete().queue()
    }
}