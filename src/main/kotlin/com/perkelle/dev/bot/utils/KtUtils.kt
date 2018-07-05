package com.perkelle.dev.bot.utils

import com.perkelle.dev.bot.Constants
import com.perkelle.dev.bot.datastores.tables.settings.AutoDeleteMessages
import com.perkelle.dev.bot.getBot
import com.perkelle.dev.bot.managers.getWrapper
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.tools.io.MessageInput
import com.sedmelluq.discord.lavaplayer.tools.io.MessageOutput
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.entities.MessageChannel
import net.dv8tion.jda.core.entities.TextChannel
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import java.util.concurrent.TimeUnit

fun MessageChannel.sendEmbed(title: String, message: String, color: Int, inline: Boolean = false, autoDelete: Boolean = true, callback: Callback<Message> = {}) {
    sendMessage(EmbedBuilder()
            .setColor(color)
            .setThumbnail(lazy {
                if(this is TextChannel && this.guild.getWrapper().isPremium()) this.guild.iconUrl
                else getBot().pictureURL
            }.value)
            .addField(title, message, inline)
            .build())
            .queue {
                callback(it)
                if(autoDelete) it.deleteAfter()
            }
}

fun MessageChannel.sendEmbed(title: String, message: String, color: Colors = Colors.GREEN, inline: Boolean = false, autoDelete: Boolean = true, callback: Callback<Message> = {}) = sendEmbed(title, message, color.denary, inline, autoDelete, callback)

fun MessageChannel.sendEmbed(message: String, color: Colors = Colors.GREEN, autoDelete: Boolean = true, callback: Callback<Message> = {}) {
    sendMessage(EmbedBuilder()
            .setColor(color.denary)
            .setDescription(message)
            .setThumbnail(lazy {
                if(this is TextChannel && this.guild.getWrapper().isPremium()) this.guild.iconUrl
                else getBot().pictureURL
            }.value)
            .build())
            .queue {
                callback(it)
                if(autoDelete) it.deleteAfter()
            }
}

fun Message.deleteAfter(millis: Long = Constants.MESSAGE_DELETE_MILLIS) {
    launch {
        if(AutoDeleteMessages.isAutoDelete(guild.idLong)) {
            delay(millis)
            delete().queue()
        }
    }
}

fun MessageChannel.sendPlain(contents: String) = sendMessage(contents).queue { it.deleteAfter() }

enum class Colors(val denary: Int) {
    GREEN(2335514),
    RED(11010048),
    ORANGE(16740864),
    LIME(7658240),
    BLUE(472219),
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

fun AudioTrack.encodeTrack(playerManager: AudioPlayerManager): String {
    val stream = ByteArrayOutputStream()
    playerManager.encodeTrack(MessageOutput(stream), this)
    return Base64.getEncoder().encodeToString(stream.toByteArray())
}

fun String.decodeTrack(playerManager: AudioPlayerManager): AudioTrack {
    val bytes = Base64.getDecoder().decode(this)
    val stream = ByteArrayInputStream(bytes)
    return playerManager.decodeTrack(MessageInput(stream)).decodedTrack
}

fun<T> List<T>.with(element: T): List<T> {
    val clone = mutableListOf<T>()
    clone.addAll(this)
    clone.add(element)
    return clone
}

fun<T> List<T>.without(element: T): List<T> {
    val clone = mutableListOf<T>()
    clone.addAll(this)
    if(clone.contains(element)) clone.remove(element)
    return clone
}

fun generateRandomString(length: Int, charset: String = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"): String {
    var s = ""

    for(i in 0 until length) {
        s += charset[ThreadLocalRandom.current().nextInt(0, charset.length)]
    }

    return s
}