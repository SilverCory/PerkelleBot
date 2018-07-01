package com.perkelle.dev.bot.listeners

import com.perkelle.dev.bot.Constants
import com.perkelle.dev.bot.datastores.tables.starboard.StarboardAmounts
import com.perkelle.dev.bot.datastores.tables.starboard.StarboardChannels
import com.perkelle.dev.bot.utils.Callback
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.entities.Member
import net.dv8tion.jda.core.entities.MessageReaction
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent
import net.dv8tion.jda.core.hooks.EventListener
import net.dv8tion.jda.core.hooks.ListenerAdapter
import java.text.SimpleDateFormat
import java.util.*

fun addReactCallback(message: Long, callback: Callback<Reaction>) {
    ReactListener.callbacks[message] = callback
    launch {
        delay(Constants.MESSAGE_DELETE_MILLIS)
        removeCallback(message)
    }
}

fun removeCallback(message: Long) = ReactListener.callbacks.remove(message)

class ReactListener: ListenerAdapter(), EventListener {

    companion object {
        val callbacks = mutableMapOf<Long, Callback<Reaction>>()
    }

    private val dateFormat = SimpleDateFormat("HH':'mm dd '/' MM '/' yyyy")

    override fun onMessageReactionAdd(e: MessageReactionAddEvent) {
        val member = e.member
        val id = e.messageIdLong
        val channel = e.channel
        val guild = e.guild

        if(member != e.guild.selfMember && callbacks.containsKey(id)) {
            callbacks[id]?.invoke(Reaction(member, e.reactionEmote))
        }

        if(e.reactionEmote.name == "⭐") {
            launch {
                val starboardChannelId = StarboardChannels.getStarboardChannel(guild.idLong) ?: return@launch
                val starboardChannel = guild.getTextChannelById(starboardChannelId) ?: return@launch

                val starsRequired = StarboardAmounts.getAmount(guild.idLong)
                val stars = e.reaction.users.complete().size

                val msg = channel.getMessageById(id).complete()

                if(stars >= starsRequired) {
                    starboardChannel.sendMessage(
                            EmbedBuilder()
                                    .setAuthor(msg.member.effectiveName, null, msg.member.user.effectiveAvatarUrl)
                                    .setDescription(msg.contentStripped)
                                    .setColor(16760576)
                                    .setFooter("Message ID: $id | Posted: ${dateFormat.format(Date(msg.creationTime.toInstant().toEpochMilli()))}", null)
                                    .build()
                    ).queue()
                }
            }
        }
    }
}

data class Reaction(val member: Member, val emote: MessageReaction.ReactionEmote)