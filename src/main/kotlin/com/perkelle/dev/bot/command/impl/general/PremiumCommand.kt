package com.perkelle.dev.bot.command.impl.general

import com.perkelle.dev.bot.command.CommandBuilder
import com.perkelle.dev.bot.command.CommandCategory
import com.perkelle.dev.bot.command.ICommand
import com.perkelle.dev.bot.command.PermissionCategory
import com.perkelle.dev.bot.datastores.tables.PremiumKeys
import com.perkelle.dev.bot.datastores.tables.PremiumUsers
import com.perkelle.dev.bot.utils.Colors
import com.perkelle.dev.bot.utils.sendEmbed

class PremiumCommand: ICommand {

    override fun register() {
        CommandBuilder()
                .setName("premium")
                .setDescription("Activate premium on your account")
                .setCategory(CommandCategory.GENERAL)
                .setPermission(PermissionCategory.GENERAL)
                .setExecutor {
                    if(args.isEmpty()) {
                        channel.sendEmbed("Premium", "Join to support guild (`p!support`) and visit the #premium channel for more information on Perkelle Bot Premium\n" +
                                "Already got a key? Type `p!premium KEY_HERE` to activate it!")
                        return@setExecutor
                    }

                    val key = args[0].toLowerCase()
                    PremiumKeys.isValid(key) { valid ->
                        if(!valid) {
                            channel.sendEmbed("Premium", "Invalid key. Perhaps you copied it incorrectly? Join the support guild (`p!support`) for further assistance.", Colors.RED)
                            return@isValid
                        }

                        PremiumKeys.removeKey(key)
                        PremiumKeys.getMonths(key) { months ->
                            PremiumUsers.addMonths(user.idLong, months)
                            channel.sendEmbed("Premium", "You have activated your key, granting you `$months` months of premium features in all the guilds you own")
                        }
                    }
                }
    }
}