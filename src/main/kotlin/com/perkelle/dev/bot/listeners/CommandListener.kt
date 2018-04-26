package com.perkelle.dev.bot.listeners

import com.perkelle.dev.bot.Constants
import com.perkelle.dev.bot.command.CommandBuilder
import com.perkelle.dev.bot.command.CommandContext
import com.perkelle.dev.bot.command.hasPermission
import com.perkelle.dev.bot.getConfig
import com.perkelle.dev.bot.managers.getWrapper
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
        val guildWrapper = guild.getWrapper()
        val channel = e.channel
        val sender = e.member ?: return //Webhook fun
        val user = sender.user ?: return
        val msg = e.message
        val content = msg.contentRaw
        val self = guild.selfMember

        if(requiredPermissions.any { !self.hasPermission(channel, it) }) return

        val customPrefix = guildWrapper.prefix
        if(!content.startsWith(getConfig().getDefaultPrefix(), true) && !content.startsWith(customPrefix ?: getConfig().getDefaultPrefix(), true)) return
        val usedPrefix =
                if(customPrefix != null && content.startsWith(customPrefix, true)) customPrefix
                else getConfig().getDefaultPrefix()

        val split = content.split(" ")
        val root = split[0].substring(usedPrefix.length)
        val args = split.toTypedArray().copyOfRange(1, split.size)

        if(!root.equals("togglechannel", true) && guildWrapper.disabledChannels.contains(channel.idLong)) return

        launch {
            delay(Constants.MESSAGE_DELETE_MILLIS)
            if(self.hasPermission(channel, Permission.MESSAGE_MANAGE)) msg.delete().queue()
        }

        val toExecute by lazy {
            val cmd = commands.firstOrNull { it.name.equals(root, true) || it.aliases.any { it.equals(root, true) } } ?: return@lazy null
            val subCmds = mutableListOf(cmd)
            var argNum = 1

            while(true) {
                subCmds.add(subCmds.lastOrNull()?.children?.firstOrNull { if(split.size - 1 >= argNum) it.name.equals(split[argNum], true) else false} ?: break)
                argNum++
            }

            subCmds.last() to lazy {
                if(args.isNotEmpty()) args.copyOfRange(argNum-1, args.size)
                else arrayOf()
            }.value
        }

        if(toExecute == null) return

        val subCmd = toExecute.first
        val subArgs = toExecute.second

        if(subCmd.botAdminOnly && !getConfig().getAdminIds().contains(user.idLong)) {
            channel.sendEmbed(Constants.NO_PERMISSION, Colors.RED)
            return
        }

        if(!sender.hasPermission(subCmd.permissionCategory)) {
            channel.sendEmbed(Constants.NO_PERMISSION, Colors.RED)
            return
        }

        subCmd.executor(CommandContext(user, sender, guild, channel, msg, subArgs))
    }
}