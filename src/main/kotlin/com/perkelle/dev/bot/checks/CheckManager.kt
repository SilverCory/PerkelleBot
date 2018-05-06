package com.perkelle.dev.bot.checks

import com.perkelle.dev.bot.checks.impl.InviteCheck

class CheckManager {

    companion object {
        val checks = listOf<Check>(
                InviteCheck()
        )
    }
}