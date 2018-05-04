package com.perkelle.dev.bot.command.impl.general

import com.perkelle.dev.bot.command.CommandBuilder
import com.perkelle.dev.bot.command.CommandCategory
import com.perkelle.dev.bot.command.ICommand
import com.perkelle.dev.bot.command.PermissionCategory
import com.perkelle.dev.bot.utils.sendEmbed

class AboutCommand: ICommand {

    override fun register() {
        CommandBuilder()
                .setName("about")
                .setDescription("Gives you information about the bot")
                .setCategory(CommandCategory.GENERAL)
                .setPermission(PermissionCategory.GENERAL)
                .setExecutor {
                    channel.sendEmbed("About", """
                        Prefix: `p!`
                        Help: `p!help`
                        Invite: `p!invite`
                        Support: `p!support`
                        Github: `https://github.com/Dot-Rar/PerkelleBot`
                        Sponsors: `https://deluxenode.com, https://perkelle.com`
                    """.trimIndent())
                }
    }
}