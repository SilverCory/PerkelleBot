package com.perkelle.dev.bot.datastores.tables

import com.perkelle.dev.bot.datastores.upsert
import com.perkelle.dev.bot.utils.onComplete
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.concurrent.TimeUnit

object PremiumUsers {

    data class PremiumUser(val id: Long, val isPremium: Boolean, val expire: Long?)

    private val cache = mutableListOf<PremiumUser>()

    object Store: Table("premiumusers") {
        val user = long("user").uniqueIndex().primaryKey()
        val expire = long("expiretime")
    }

    fun isPremium(id: Long, callback: (Boolean) -> Unit) {
        val user = cache.firstOrNull { it.id == id }

        if(user == null) {
            async {
                transaction {
                    Store.select {
                        Store.user eq id
                    }.map { PremiumUser(id, it[Store.expire] > System.currentTimeMillis(), it[Store.expire]) }.firstOrNull() ?: PremiumUser(id, false, null)
                }
            }.onComplete {
                cache.add(it)
                callback(it.isPremium)
            }
        } else {
            if(user.isPremium) {
                if(System.currentTimeMillis() > user.expire!!) {
                    cache.remove(user)
                    cache.add(PremiumUser(id, false, null))
                    setPremium(id, false, null)
                    callback(false)
                }
                else callback(true)
            }
            else callback(false)
        }
    }

    fun setPremium(id: Long, premium: Boolean, months: Int?) {
        launch {
            if(premium) {
                transaction {
                    Store.upsert(listOf(Store.expire)) {
                        it[user] = id
                        it[expire] = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(30L * months!!)
                    }
                }
            } else {
                transaction {
                    Store.deleteWhere {
                        Store.user eq id
                    }
                }
            }
        }
    }

    fun getExpire(id: Long, callback: (Long?) -> Unit) {
        async {
            transaction {
                Store.select {
                    Store.user eq id
                }.map { it[Store.expire] }.firstOrNull()
            }
        }.onComplete(callback)
    }

    fun addMonths(id: Long, months: Int) {
        launch {
            isPremium(id) { hasPremium ->
                getExpire(id) { millis ->
                    val newExpire by lazy {
                        if(millis == null) System.currentTimeMillis() + TimeUnit.DAYS.toMillis(months * 30L)
                        else millis + TimeUnit.DAYS.toMillis(months * 30L)
                    }

                    cache.removeAll { it.id == id }
                    cache.add(PremiumUser(id, true, newExpire))

                    transaction {
                        Store.upsert(listOf(Store.expire)) {
                            it[Store.user] = id
                            it[Store.expire] = newExpire
                        }
                    }
                }
            }
        }
    }
}