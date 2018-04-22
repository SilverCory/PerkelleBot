package com.perkelle.dev.bot.command

import net.dv8tion.jda.core.entities.*
import java.util.*

data class CommandContext(val user: User, val member: Member, val guild: Guild, val channel: MessageChannel, val message: Message, val args: Array<String>) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CommandContext

        if (user != other.user) return false
        if (member != other.member) return false
        if (guild != other.guild) return false
        if (channel != other.channel) return false
        if (message != other.message) return false
        if (!Arrays.equals(args, other.args)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = user.hashCode()
        result = 31 * result + member.hashCode()
        result = 31 * result + guild.hashCode()
        result = 31 * result + channel.hashCode()
        result = 31 * result + message.hashCode()
        result = 31 * result + Arrays.hashCode(args)
        return result
    }
}