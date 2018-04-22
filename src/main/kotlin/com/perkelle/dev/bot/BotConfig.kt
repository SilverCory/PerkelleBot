package com.perkelle.dev.bot

import com.perkelle.dev.bot.wrappers.FileName
import com.perkelle.dev.bot.wrappers.JSON
import org.json.JSONArray

@FileName
class BotConfig: JSON() {

    fun getToken() = config.getGenericOrNull<String>("token")

    fun getTotalShards() = config.getGeneric("total-shards", -1)
    fun getLowestShard() = config.getGeneric("lowest-shard", 0)

    fun getDefaultPrefix() = config.getGeneric("default-prefix", "p!")

    fun getAdminIds() = config.getGeneric("admin-ids", JSONArray()).toList().map { it.toString().toLong() }

    fun getSupportGuild() = config.getGeneric("support-guild", "")
}