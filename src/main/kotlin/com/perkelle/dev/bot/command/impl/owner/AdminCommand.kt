package com.perkelle.dev.bot.command.impl.owner

import com.perkelle.dev.bot.PerkelleBot
import com.perkelle.dev.bot.command.CommandBuilder
import com.perkelle.dev.bot.command.ICommand
import com.perkelle.dev.bot.command.PermissionCategory
import com.perkelle.dev.bot.datastores.tables.premium.PremiumKeys
import com.perkelle.dev.bot.getBot
import com.perkelle.dev.bot.getConfig
import com.perkelle.dev.bot.managers.getWrapper
import com.perkelle.dev.bot.utils.Colors
import com.perkelle.dev.bot.utils.sendEmbed
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import java.util.*
import javax.script.ScriptEngineManager
import javax.script.ScriptException

class AdminCommand: ICommand {

    private val engineManager = ScriptEngineManager()

    override fun register() {
        val cmdBuilder = CommandBuilder()
                .setName("admin")
                .setAliases("a")
                .setDescription("Admin only commands")
                .setBotAdminOnly(true)
                .setPermission(PermissionCategory.ADMIN)
                .setExecutor {
                    channel.sendEmbed("Admin", "You need to specify a subcommand", Colors.RED)
                }

        cmdBuilder.addChild(
                CommandBuilder(true, cmdBuilder)
                        .setName("eval")
                        .setDescription("Runs a JS eval")
                        .setBotAdminOnly(true)
                        .setExecutor {
                            val engine = engineManager.getEngineByName("nashorn")

                            engine.put("jda", guild.jda)
                            engine.put("bot", PerkelleBot.instance)
                            engine.put("shardmanager", getBot().shardManager)
                            engine.put("guild", guild)
                            engine.put("wrapper", guild.getWrapper())
                            engine.put("channel", channel)
                            engine.put("user", user)
                            engine.put("sender", sender)
                            engine.put("msg", message)

                            try {
                                val result = engine.eval(args.joinToString(" "))
                                if(result != null) channel.sendEmbed("Admin", result.toString())
                            } catch(ex: ScriptException) {
                                channel.sendEmbed("Admin", ex.message ?: "Error while performing eval", Colors.RED)
                            }
                        }
        ).addChild(CommandBuilder(true, cmdBuilder)
                .setName("shutdown")
                .setDescription("Shuts the bot down gracefully")
                .setBotAdminOnly(true)
                .setExecutor {
                    channel.sendEmbed("Admin", "Shutting down")

                    getBot().shardManager.shards.forEach { it.shutdownNow() }
                    getBot().shardManager.shutdown()

                    launch {
                        delay(2000)
                        System.exit(0)
                    }
                }
        ).addChild(CommandBuilder(true, cmdBuilder)
                .setName("sr")
                .setDescription("Restart a shard")
                .setBotAdminOnly(true)
                .setExecutor {
                    if(args.isEmpty() || args[0].toIntOrNull() == null || PerkelleBot.instance.shardManager.shards.none { it.shardInfo.shardId == args[0].toInt() }) {
                        channel.sendEmbed("Admin", "Restarting this shard")
                        getBot().shardManager.restart(guild.jda.shardInfo.shardId)
                    } else {
                        channel.sendEmbed("Admin", "Restarting shard `${args[0]}`")
                        getBot().shardManager.restart(args[0].toInt())
                    }
                }
        ).addChild(CommandBuilder(true, cmdBuilder)
                .setName("restart")
                .setDescription("Restarts all shards")
                .setBotAdminOnly(true)
                .setExecutor {
                    channel.sendEmbed("Admin", "Restarting all shards")
                    getBot().shardManager.restart()
                }
        ).addChild(CommandBuilder(true, cmdBuilder)
                .setName("genpremium")
                .setDescription("Generates a premium key")
                .setBotAdminOnly(true)
                .setExecutor {
                    if(args.isEmpty() || args[0].toIntOrNull() == null || args[0].toInt() < 1) {
                        channel.sendEmbed("Admin", "You need to specify an amount of months", Colors.RED)
                        return@setExecutor
                    }

                    val months = args[0].toInt()
                    val keys = mutableListOf<String>()

                    if(args.size >= 2 && args[1].toIntOrNull() != null && args[1].toInt() > 0) {
                        for(i in 1..args[1].toInt()) keys.add(UUID.randomUUID().toString())
                    } else keys.add(UUID.randomUUID().toString())

                    keys.forEach { PremiumKeys.addKey(it, months) }
                    user.openPrivateChannel().queue { it.sendEmbed("Admin", "Generated new premium keys:\n```${keys.joinToString("\n")}```\n -> `$months months`", autoDelete = false) }
                }
        ).addChild(CommandBuilder(true, cmdBuilder)
                .setName("reloadconfig")
                .setDescription("Reloads the config")
                .setBotAdminOnly(true)
                .setExecutor {
                    getConfig().reload()
                    channel.sendEmbed("Admin", "Reloaded config")
                }
        )
    }
}