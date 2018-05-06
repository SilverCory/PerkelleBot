package com.perkelle.dev.bot.checks

import net.dv8tion.jda.core.entities.*

data class MessageContext(val sender: Member, val user: User, val guild: Guild, val channel: TextChannel, val message: Message, val content: String)