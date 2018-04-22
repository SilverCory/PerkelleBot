package com.perkelle.dev.bot.listeners

import com.perkelle.dev.bot.Constants
import com.perkelle.dev.bot.utils.Callback
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import net.dv8tion.jda.core.entities.Member
import net.dv8tion.jda.core.entities.MessageReaction
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent
import net.dv8tion.jda.core.hooks.EventListener
import net.dv8tion.jda.core.hooks.ListenerAdapter

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

    override fun onMessageReactionAdd(e: MessageReactionAddEvent) {
        val member = e.member
        val id = e.messageIdLong

        if(member != e.guild.selfMember && callbacks.containsKey(id)) {
            callbacks[id]?.invoke(Reaction(member, e.reactionEmote))
        }
    }
}

data class Reaction(val member: Member, val emote: MessageReaction.ReactionEmote)