package com.perkelle.dev.bot.wrappers.redis

import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig
import redis.clients.jedis.JedisPubSub

class RedisWrapper(simpleConfig: RedisConfig) {

    private val pool: JedisPool

    init {
        val config = JedisPoolConfig()

        config.maxTotal = simpleConfig.poolSettings.maxTotal
        config.maxIdle = simpleConfig.poolSettings.maxIdle

        config.testOnBorrow = simpleConfig.poolSettings.tests
        config.testOnReturn = simpleConfig.poolSettings.tests
        config.testOnCreate = simpleConfig.poolSettings.tests
        config.testWhileIdle=  simpleConfig.poolSettings.tests
        config.testWhileIdle=  simpleConfig.poolSettings.tests

        config.blockWhenExhausted = true

        pool =
                if(simpleConfig.connectionSettings.password == null) JedisPool(config, simpleConfig.connectionSettings.host, simpleConfig.connectionSettings.port, simpleConfig.timeoutSettings.timeout, simpleConfig.connectionSettings.ssl)
                else JedisPool(config, simpleConfig.connectionSettings.host, simpleConfig.connectionSettings.port, simpleConfig.timeoutSettings.timeout, simpleConfig.connectionSettings.password, simpleConfig.connectionSettings.ssl)
    }

    fun disable() {
        pool.close()
        pool.destroy()
    }

    fun getConnection() = pool.resource

    fun execute(block: (RedisWrapper) -> Unit) {
        block(this)
    }

    fun set(key: String, value: Any, autoClose: Boolean = true) {
        val jedis = getConnection()
        jedis.set(key, value.toString())
        if(autoClose) jedis.close()
    }

    fun subscribe(channel: String, callback: (String) -> Unit) {
        val jedis = getConnection()
        jedis.subscribe(object: JedisPubSub() {
            override fun onMessage(receivingChannel: String, message: String) {
                callback(message)
            }
        }, "premiumupdates")
    }

    fun publish(channel: String, message: String) {
        val jedis = getConnection()
        jedis.publish(channel, message)
        jedis.close()
    }

    fun Jedis.execute(block: (Jedis) -> Unit, autoClose: Boolean) {
        block(this@execute)
        if(autoClose) this@execute.close()
    }

    fun Jedis.executeQuery(block: (Jedis) -> String, autoClose: Boolean): String {
        val res = block(this@executeQuery)
        if(autoClose) this@executeQuery.close()
        return res
    }
}