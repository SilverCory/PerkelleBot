package com.perkelle.dev.bot.command.impl.general

import com.perkelle.dev.bot.command.CommandBuilder
import com.perkelle.dev.bot.command.CommandCategory
import com.perkelle.dev.bot.command.ICommand
import com.perkelle.dev.bot.command.PermissionCategory
import com.perkelle.dev.bot.getBot
import com.perkelle.dev.bot.managers.getWrapper
import com.perkelle.dev.bot.utils.Colors
import com.perkelle.dev.bot.utils.deleteAfter
import net.dv8tion.jda.core.EmbedBuilder

class SponsorsCommand: ICommand {

    override fun register() {
        CommandBuilder()
                .setName("sponsors")
                .setDescription("Shows you the list of people that who have helped to make the bot possible")
                .setAliases("partners", "sponsor", "partner")
                .setCategory(CommandCategory.GENERAL)
                .setPermission(PermissionCategory.GENERAL)
                .setExecutor {
                    channel.sendMessage(
                            EmbedBuilder()
                                    .setColor(Colors.GREEN.denary)
                                    .setThumbnail(lazy {
                                        if(guild.getWrapper().isPremium()) guild.iconUrl
                                        else getBot().pictureURL
                                    }.value)
                                    .setTitle("Sponsors")
                                    .addField("DeluxeNode", "DeluxeNode is a Minecraft, Web and VPS host with owned, high quality hardware starting from €1 / month. Their servers feature NVME SSDs, Threadripper CPUs and DDR4 RAM and 1660 GBPS DDoS protection.", true)
                                    .build()
                    ).queue { it.deleteAfter() }
                }
    }
}