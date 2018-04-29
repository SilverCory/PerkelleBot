package com.perkelle.dev.bot.wrappers.redis

data class RedisConfig(val connectionSettings: ConnectionSettings = ConnectionSettings(), val poolSettings: PoolSettings = PoolSettings(), val timeoutSettings: TimeoutSettings = TimeoutSettings())

data class ConnectionSettings(val host: String = "localhost", val port: Int = 6379, val password: String? = null, val ssl: Boolean = false)

data class PoolSettings(val maxTotal: Int = 128, val maxIdle: Int = 128, val tests: Boolean = true)

data class TimeoutSettings(val timeout: Int = 10)