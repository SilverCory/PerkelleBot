package com.perkelle.dev.bot.checks.impl

import com.perkelle.dev.bot.checks.Check
import com.perkelle.dev.bot.checks.MessageContext
import com.perkelle.dev.bot.datastores.tables.checks.BlockInvites

class InviteCheck: Check {

    override val regex = Regex("(?:http|https)://discord\\.gg/\\w*")

    override fun MessageContext.onViolate() {
        if(guild.selfMember.canInteract(sender) && !user.isBot) message.delete().queue()
    }

    override fun isEnabled(guild: Long): Boolean {
        return BlockInvites.isBlocked(guild)
    }
}