package com.perkelle.dev.bot.command.datastores

import com.perkelle.dev.bot.getConfig
import com.sxtanna.database.Kedis
import com.sxtanna.database.config.KedisConfig
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

    private lateinit var kedis: Kedis

    fun setup() {
        val config = KedisConfig(
                KedisConfig.ServerOptions(getConfig().getRedisHost(), getConfig().getRedisPort()),
                lazy {
                    if(getConfig().getRedisAuth().isEmpty()) KedisConfig.UserOptions()
                    else KedisConfig.UserOptions(getConfig().getRedisAuth())
                }.value,
                KedisConfig.PoolOptions(getConfig().getRedisPoolSize(), getConfig().getRedisIdleSize(), getConfig().getRedisTimeout()))

        kedis = Kedis(config)
        kedis.enable()
    }

    fun disable() {
        kedis.disable()
    }

    object ShardStatus {

        private var publishConnection: Jedis? = null

        fun setStatus(id: Int, status: String) {
            launch {
                instance.kedis {
                    set("shard:$id", status)
                }
            }
        }

        fun broadcastUpdate(id: Int, status: String) {
            if(publishConnection == null) publishConnection = instance.kedis.resource()

            launch {
                    publishConnection!!.publish("shardupdates", "$id:$status")
            }
        }
    }
}