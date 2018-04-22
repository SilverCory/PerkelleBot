package com.perkelle.dev.bot.utils

import com.perkelle.dev.bot.Constants
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.entities.MessageChannel

fun MessageChannel.sendEmbed(title: String, message: String, color: Int = Colors.GREEN, inline: Boolean = false, autoDelete: Boolean = true, callback: Callback<Message> = {}) {
    sendMessage(EmbedBuilder()
            .setColor(color)
            .addField(title, message, inline)
            .build())
            .queue {
                callback(it)
                if(autoDelete) {
                    launch {
                        delay(Constants.MESSAGE_DELETE_MILLIS)
                        it.delete().queue()
                    }
                }
            }
}

fun MessageChannel.sendEmbed(message: String, color: Int = Colors.GREEN, autoDelete: Boolean = true, callback: Callback<Message> = {}) {
    sendMessage(EmbedBuilder()
            .setColor(color)
            .setDescription(message)
            .build())
            .queue {
                callback(it)
                if(autoDelete) {
                    launch {
                        delay(Constants.MESSAGE_DELETE_MILLIS)
                        it.delete().queue()
                    }
                }
            }
}


object Colors {
    val GREEN = 2335514
    val RED = 11010048
}