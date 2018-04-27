package com.perkelle.dev.bot.utils

import com.perkelle.dev.bot.Constants
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.entities.MessageChannel
import java.util.concurrent.TimeUnit

fun MessageChannel.sendEmbed(title: String, message: String, color: Int = Colors.GREEN, inline: Boolean = false, autoDelete: Boolean = true, callback: Callback<Message> = {}) {
    sendMessage(EmbedBuilder()
            .setColor(color)
            .addField(title, message, inline)
            .build())
            .queue {
                callback(it)
                if(autoDelete) it.deleteAfter()
            }
}

fun MessageChannel.sendEmbed(message: String, color: Int = Colors.GREEN, autoDelete: Boolean = true, callback: Callback<Message> = {}) {
    sendMessage(EmbedBuilder()
            .setColor(color)
            .setDescription(message)
            .build())
            .queue {
                callback(it)
                if(autoDelete) it.deleteAfter()
            }
}

fun Message.deleteAfter(millis: Long = Constants.MESSAGE_DELETE_MILLIS) {
    launch {
        delay(millis)
        delete().queue()
    }
}

object Colors {
    val GREEN = 2335514
    val RED = 11010048
}

fun<T> Deferred<T>.onComplete(block: (T) -> Unit) = invokeOnCompletion { block(getCompleted()) }
fun<T> Deferred<T?>.onCompleteOrNull(block: (T?) -> Unit) {
    invokeOnCompletion {
        block(this.getCompleted())
    }
    runBlocking {
        this@onCompleteOrNull.join()
    }
}

fun<T> MutableList<T>.add(vararg elements: T) = elements.forEach { add(it) }

fun String.toCamelCase(): String {
    var final = ""
    val split = split("_")
    final += split[0]
    if(split.size > 1) split.toTypedArray().copyOfRange(1, split.size).forEach { final += it.capitalize() }
    return final
}

fun boolValue(str: String): Boolean {
    return when(str.toLowerCase()) {
        "true" -> true
        "false" -> false
        else -> throw IllegalArgumentException()
    }
}

fun Long.formatMillis() = String.format("%d min, %d sec",
        TimeUnit.MILLISECONDS.toMinutes(this),
        TimeUnit.MILLISECONDS.toSeconds(this) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(this)))