package com.perkelle.dev.bot.command.impl.general

import com.perkelle.dev.bot.command.CommandBuilder
import com.perkelle.dev.bot.command.CommandCategory
import com.perkelle.dev.bot.command.ICommand
import com.perkelle.dev.bot.command.PermissionCategory
import com.perkelle.dev.bot.datastores.tables.premium.PremiumKeys
import com.perkelle.dev.bot.datastores.tables.premium.PremiumUsers
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
                        channel.sendEmbed("Premium", "Visit <https://bot.perkelle.com/premium.php> for more information on premium\n" +
                                "Already got a key? Type `p!premium KEY_HERE` to activate it!")
                        return@setExecutor
                    }

                    val key = args[0].toLowerCase()
                    val valid = PremiumKeys.isValid(key)

                    if(!valid) {
                        channel.sendEmbed("Premium", "Invalid key. Perhaps you copied it incorrectly? Join the support guild (`p!support`) for further assistance.", Colors.RED)
                        return@setExecutor
                    }

                    val months = PremiumKeys.getMonths(key)
                    PremiumKeys.removeKey(key)
                    PremiumUsers.addMonths(user.idLong, months)
                    channel.sendEmbed("Premium", "You have activated your key, granting you `$months` months of premium features in all the guilds you own")
                }
    }
}