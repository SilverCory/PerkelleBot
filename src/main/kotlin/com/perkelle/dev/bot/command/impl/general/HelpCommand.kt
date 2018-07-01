package com.perkelle.dev.bot.command.impl.general

import com.perkelle.dev.bot.command.*
import com.perkelle.dev.bot.listeners.CommandListener
import com.perkelle.dev.bot.utils.Colors
import com.perkelle.dev.bot.utils.sendEmbed
import net.dv8tion.jda.core.EmbedBuilder

class HelpCommand: ICommand {

    override fun register() {
        CommandBuilder()
                .setName("help")
                .setDescription("Shows you a list of all commands")
                .setCategory(CommandCategory.GENERAL)
                .setPermission(PermissionCategory.GENERAL)
                .setAliases("h")
                .setExecutor {
                    channel.sendEmbed("Help", "Check your PMs")

                    val message = EmbedBuilder()
                            .setColor(Colors.GREEN.denary)

                    CommandCategory.values().forEach { category ->
                        val all = mutableListOf<CommandBuilder>()

                        CommandListener.commands.filter { it.category == category && sender.hasPermission(it.permissionCategory) && (!it.botAdminOnly || (it.botAdminOnly && user.isGlobalAdmin())) }.forEach { parent ->
                            all.add(parent)
                            all.addAll(parent.children)
                        }

                        val contents = all.joinToString("\n") {  "`${if(it.isChild) it.parent!!.name + " " else ""}${it.name}` - ${it.description}" }
                        if(contents.isNotEmpty()) message.addField(category.name.capitalize(), contents, false)
                    }

                    user.openPrivateChannel().queue { it.sendMessage(message.build()).queue() }
                }
    }
}