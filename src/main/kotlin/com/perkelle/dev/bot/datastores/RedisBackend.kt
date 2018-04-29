package com.perkelle.dev.bot.datastores

import com.perkelle.dev.bot.getConfig
import com.perkelle.dev.bot.wrappers.redis.*
import kotlinx.coroutines.experimental.launch
import redis.clients.jedis.Jedis

fun getRedisBackend() = RedisBackend.instance

class RedisBackend {

    companion object {
        lateinit var instance: RedisBackend
    }

    init {
        instance = this
    }

    private lateinit var redis: RedisWrapper

    fun setup() {
        val config = RedisConfig(
                ConnectionSettings(getConfig().getRedisHost(), getConfig().getRedisPort(), getConfig().getRedisAuth()),
                PoolSettings(getConfig().getRedisPoolSize(), getConfig().getRedisIdleSize()),
                TimeoutSettings(getConfig().getRedisTimeout())
        )

        redis = RedisWrapper(config)
    }

    fun disable() {
        redis.disable()
    }

    object ShardStatus {

        private var publishConnection: Jedis? = null

        fun setStatus(id: Int, status: String) {
            instance.redis.set("shard:$id", status)
        }

        fun broadcastUpdate(id: Int, status: String) {
            if(publishConnection == null) publishConnection = instance.redis.getConnection()

            launch {
                publishConnection!!.publish("shardupdates", "$id:$status")
            }
        }
    }
}