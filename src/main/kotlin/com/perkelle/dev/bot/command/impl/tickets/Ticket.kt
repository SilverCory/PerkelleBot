package com.perkelle.dev.bot.command.impl.tickets

data class Ticket(val guild: Long, val channel: Long, val id: String, val open: Boolean, val owner: Long)