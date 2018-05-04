package com.perkelle.dev.bot.datastores

import com.perkelle.dev.bot.datastores.tables.PremiumUsers
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

    object ServerCount {

        private var publishConnection: Jedis? = null

        fun setCount(shard: Int, count: Int) {
            instance.redis.set("servercount:$shard", count)
        }

        fun broadcastUpdate(id: Int, count: Int) {
            if(publishConnection == null) publishConnection = instance.redis.getConnection()

            launch {
                publishConnection!!.publish("countupdates", "$id:$count")
            }
        }
    }

    object PremiumUpdates {

        fun onPremiumPurchase(callback: (PremiumUsers.PremiumUser) -> Unit) {
            instance.redis.subscribe("premiumupdates") {
                val id = it.split(":")[0].toLongOrNull() ?: return@subscribe
                val expire = it.split(":")[1].toLongOrNull() ?: return@subscribe

                callback(PremiumUsers.PremiumUser(id, expire > System.currentTimeMillis(), expire))
            }
        }

        fun publishUpdate(user: PremiumUsers.PremiumUser) {
            instance.redis.publish("premiumupdates", "${user.id}:${user.expire}")
        }
    }
}