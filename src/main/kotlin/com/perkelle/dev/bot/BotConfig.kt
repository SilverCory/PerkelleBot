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
    fun getInviteLink() = config.getGeneric("invite-link", "https://bot.perklle.com/invite")

    fun getRedisHost() = config.getGeneric("redis.host", "localhost")
    fun getRedisPort() = config.getGeneric("redis.port", 6379)
    fun getRedisAuth() = config.getGeneric("redis.auth", "")
    fun getRedisPoolSize() = config.getGeneric("redis.maxsize", 200)
    fun getRedisIdleSize() = config.getGeneric("redis.idlesize", 200)
    fun getRedisTimeout() = config.getGeneric("redis.timeout", 0)

    inline fun<reified T>  getValue(key: String, default: T) = config.getGeneric(key, default)
}