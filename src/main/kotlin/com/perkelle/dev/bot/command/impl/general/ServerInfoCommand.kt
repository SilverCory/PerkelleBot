package com.perkelle.dev.bot.command.impl.general

import com.perkelle.dev.bot.command.CommandBuilder
import com.perkelle.dev.bot.command.CommandCategory
import com.perkelle.dev.bot.command.ICommand
import com.perkelle.dev.bot.command.PermissionCategory
import com.perkelle.dev.bot.getBot
import com.perkelle.dev.bot.managers.getWrapper
import com.perkelle.dev.bot.utils.deleteAfter
import net.dv8tion.jda.core.EmbedBuilder

class ServerInfoCommand: ICommand {

    override fun register() {
        CommandBuilder()
                .setName("serverinfo")
                .setDescription("Provides information about the server")
                .setCategory(CommandCategory.GENERAL)
                .setPermission(PermissionCategory.GENERAL)
                .setExecutor {
                    channel.sendMessage(EmbedBuilder()
                            .setTitle("Info")
                            .setColor(4359924).setImage(lazy {
                                if(guild.getWrapper().isPremium()) guild.iconUrl
                                else getBot().pictureURL
                            }.value)
                            .addField("Name", guild.name, true)
                            .addField("Owner", guild.owner.asMention, true)
                            .addField("Members", "${guild.members.size} (${guild.members.filter { it.user.isBot }.size} bots)", true)
                            .addField("Channels", "${guild.textChannels.size} text / ${guild.voiceChannels.size} voice", true)
                            .addField("Roles", guild.roles.size.toString(), true)
                            .addField("Voice Region", guild.region.name, true)
                            .build()).queue { it.deleteAfter() }
                }
    }
}