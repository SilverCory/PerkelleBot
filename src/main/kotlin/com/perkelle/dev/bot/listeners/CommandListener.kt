package com.perkelle.dev.bot.listeners

import com.perkelle.dev.bot.Constants
import com.perkelle.dev.bot.command.CommandBuilder
import com.perkelle.dev.bot.command.CommandContext
import com.perkelle.dev.bot.getConfig
import com.perkelle.dev.bot.utils.Colors
import com.perkelle.dev.bot.utils.sendEmbed
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.core.hooks.EventListener
import net.dv8tion.jda.core.hooks.ListenerAdapter

class CommandListener: ListenerAdapter(), EventListener {

    companion object {
        val commands = mutableListOf<CommandBuilder>()
    }

    private val requiredPermissions = mutableSetOf(
            Permission.MESSAGE_WRITE,
            Permission.MESSAGE_READ,
            Permission.MESSAGE_EMBED_LINKS,
            Permission.MESSAGE_ADD_REACTION
    )

    override fun onGuildMessageReceived(e: GuildMessageReceivedEvent) {
        val guild = e.guild
        val channel = e.channel
        val sender = e.member
        val user = sender.user
        val msg = e.message
        val content = msg.contentRaw
        val self = guild.selfMember

        if(requiredPermissions.any { !self.hasPermission(channel, it) }) return

        //TODO: Custom prefixes
        if(!content.startsWith(getConfig().getDefaultPrefix(), true)) return
        val usedPrefix = getConfig().getDefaultPrefix()

        val split = content.split(" ")
        val root = split[0].substring(usedPrefix.length)
        val args = split.toTypedArray().copyOfRange(1, split.size)

        launch {
            delay(Constants.MESSAGE_DELETE_MILLIS)
            if(self.hasPermission(channel, Permission.MESSAGE_MANAGE)) msg.delete().queue()
        }

        val toExecute by lazy {
            val cmd = commands.firstOrNull { it.name.equals(root, true) } ?: return@lazy null
            val subCmds = mutableListOf(cmd)
            var argNum = 1

            while(true) {
                subCmds.add(subCmds.last().children.firstOrNull { it.name.equals(split[argNum], true) } ?: break)
                argNum++
            }

            subCmds.last() to lazy {
                if(args.isNotEmpty()) args.copyOfRange(argNum, args.size)
                else arrayOf()
            }.value
        }

        if(toExecute == null) return

        val subCmd = toExecute.first
        val subArgs = toExecute.second

        //TODO: Permission check
        if(subCmd.botAdminOnly && !getConfig().getAdminIds().contains(user.idLong)) {
            channel.sendEmbed(Constants.NO_PERMISSION, Colors.RED)
            return
        }

        launch {
            subCmd.executor(CommandContext(user, sender, guild, channel, msg, subArgs))
        }
    }
}