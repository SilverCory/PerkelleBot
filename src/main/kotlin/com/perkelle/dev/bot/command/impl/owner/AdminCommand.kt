package com.perkelle.dev.bot.command.impl.owner

import com.perkelle.dev.bot.PerkelleBot
import com.perkelle.dev.bot.command.CommandBuilder
import com.perkelle.dev.bot.command.ICommand
import com.perkelle.dev.bot.command.PermissionCategory
import com.perkelle.dev.bot.managers.getWrapper
import com.perkelle.dev.bot.utils.Colors
import com.perkelle.dev.bot.utils.sendEmbed
import javax.script.ScriptEngineManager
import javax.script.ScriptException

class AdminCommand: ICommand {

    private val engineManager = ScriptEngineManager()

    override fun register() {
        CommandBuilder()
                .setName("admin")
                .setAliases("a")
                .setDescription("Admin only commands")
                .setBotAdminOnly(true)
                .setPermission(PermissionCategory.ADMIN)
                .setExecutor {
                    channel.sendEmbed("Admin", "You need to specify a subcommand", Colors.RED)
                }
                .addChild(
                        CommandBuilder(true)
                                .setName("eval")
                                .setDescription("Runs a JS eval")
                                .setAliases("js")
                                .setBotAdminOnly(true)
                                .setExecutor {
                                    val engine = engineManager.getEngineByName("nashorn")

                                    engine.put("jda", guild.jda)
                                    engine.put("bot", PerkelleBot.instance)
                                    engine.put("guild", guild)
                                    engine.put("wrapper", guild.getWrapper())
                                    engine.put("channel", channel)
                                    engine.put("sender", user)
                                    engine.put("sender", sender)

                                    try {
                                        val result = engine.eval(args.joinToString(" "))
                                        if(result != null) channel.sendEmbed("Admin", result.toString())
                                    } catch(ex: ScriptException) {
                                        channel.sendEmbed("Admin", ex.message ?: "Error while performing eval", Colors.RED)
                                    }
                                })
    }
}