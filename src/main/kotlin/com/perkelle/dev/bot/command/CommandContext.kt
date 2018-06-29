package com.perkelle.dev.bot.command

import net.dv8tion.jda.core.entities.*
import java.util.*

data class CommandContext(val user: User, val sender: Member, val guild: Guild, val channel: TextChannel, val message: Message, val args: Array<String>, val root: String, val commandBuilder: CommandBuilder) {

    override fun equals(other: Any?): Boolean {
        if(this === other) return true
        if(other !is CommandContext) return false
        return message == other.message
    }

    override fun hashCode(): Int {
        var result = user.hashCode()
        result = 31 * result + sender.hashCode()
        result = 31 * result + guild.hashCode()
        result = 31 * result + channel.hashCode()
        result = 31 * result + message.hashCode()
        result = 31 * result + Arrays.hashCode(args)
        return result
    }
}