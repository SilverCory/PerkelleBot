package com.perkelle.dev.bot.command.impl.admin

import com.perkelle.dev.bot.command.CommandBuilder
import com.perkelle.dev.bot.command.CommandCategory
import com.perkelle.dev.bot.command.ICommand
import com.perkelle.dev.bot.command.PermissionCategory
import com.perkelle.dev.bot.datastores.tables.BlockInvites
import com.perkelle.dev.bot.utils.sendEmbed

class BlockInvitesCommand: ICommand {

    override fun register() {
        CommandBuilder()
                .setName("blockinvites")
                .setDescription("Toggles whether invites are deleted")
                .setCategory(CommandCategory.SETTINGS)
                .setPermission(PermissionCategory.ADMIN)
                .setPremiumOnly(true)
                .setExecutor {
                    val blocked = !BlockInvites.isBlocked(guild.idLong)
                    BlockInvites.toggleBlocked(guild.idLong, blocked)

                    if(blocked) channel.sendEmbed("Block Invites", "Invites are now blocked")
                    else channel.sendEmbed("Block Invites", "Invites are no longer blocked")
                }
    }
}