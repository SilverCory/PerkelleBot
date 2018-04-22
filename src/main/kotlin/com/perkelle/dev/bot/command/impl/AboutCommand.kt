package com.perkelle.dev.bot.command.impl

import com.perkelle.dev.bot.Constants
import com.perkelle.dev.bot.command.CommandBuilder
import com.perkelle.dev.bot.command.ICommand
import com.perkelle.dev.bot.getConfig
import com.perkelle.dev.bot.utils.sendEmbed
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch

class AboutCommand: ICommand {

    override fun register() {
        CommandBuilder()
                .name("about")
                .description("Gives you information about the bot")
                .executor {
                    channel.sendEmbed("About", """
                        Prefix: `p!`
                        Invite: `p!invite`
                        Support: `p!support`
                        Github: `https://github.com/Dot-Rar/PerkelleBot`
                    """.trimIndent())
                }
    }
}