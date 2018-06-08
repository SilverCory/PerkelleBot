package com.perkelle.dev.bot.command.impl.general

import com.perkelle.dev.bot.command.CommandBuilder
import com.perkelle.dev.bot.command.CommandCategory
import com.perkelle.dev.bot.command.ICommand
import com.perkelle.dev.bot.command.PermissionCategory
import com.perkelle.dev.bot.utils.sendEmbed
import net.dv8tion.jda.core.entities.Member

class AfkCommand: ICommand {

    companion object {
        // We don't need persistent or cross-instance storage
        val afk = mutableListOf<Member>()
    }

    override fun register() {
        CommandBuilder()
                .setName("afk")
                .setPermission(PermissionCategory.GENERAL)
                .setCategory(CommandCategory.GENERAL)
                .setDescription("Sets your state to AFK")
                .setExecutor {
                    afk.add(sender)
                    channel.sendEmbed("AFK", "You have been marked as AFK")
                }
    }
}