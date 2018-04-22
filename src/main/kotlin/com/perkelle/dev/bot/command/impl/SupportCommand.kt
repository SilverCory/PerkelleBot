package com.perkelle.dev.bot.command.impl

import com.perkelle.dev.bot.Constants
import com.perkelle.dev.bot.command.CommandBuilder
import com.perkelle.dev.bot.command.ICommand
import com.perkelle.dev.bot.getConfig
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch

class SupportCommand: ICommand {

    override fun register() {
        CommandBuilder()
                .name("support")
                .description("Provides you with an invite to the support guild")
                .executor {
                    channel.sendMessage("Support guild: ${getConfig().getSupportGuild()}").queue({
                        launch {
                            delay(Constants.MESSAGE_DELETE_MILLIS)
                            it.delete()
                        }
                    })
                }
    }
}