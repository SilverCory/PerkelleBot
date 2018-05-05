package com.perkelle.dev.bot.datastores.tables

import com.perkelle.dev.bot.datastores.DataStore
import com.perkelle.dev.bot.datastores.RedisBackend
import com.perkelle.dev.bot.datastores.upsert
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.concurrent.TimeUnit

object PremiumUsers: DataStore {

    data class PremiumUser(val id: Long, val isPremium: Boolean, val expire: Long?)

    val cache = mutableListOf<PremiumUser>()

    private object Store: Table("premiumusers") {
        val user = long("user").uniqueIndex().primaryKey()
        val expire = long("expiretime")
    }

    override fun getTable() = BlacklistedMembers.Store

    fun isPremium(id: Long): Boolean {
        val user = cache.firstOrNull { it.id == id }

        if(user == null) {
            val premium = transaction {
                Store.select {
                    Store.user eq id
                }.map { PremiumUser(id, it[Store.expire] > System.currentTimeMillis(), it[Store.expire]) }.firstOrNull() ?: PremiumUser(id, false, null)
            }
            cache.add(premium)
            return premium.isPremium
        } else {
            return if(user.isPremium) {
                if(System.currentTimeMillis() > user.expire!!) {
                    cache.remove(user)
                    cache.add(PremiumUser(id, false, null))
                    setPremium(id, false, null)
                    false
                }
                else true
            }
            else false
        }
    }

    fun setPremium(id: Long, premium: Boolean, months: Int?) {
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

    fun getExpire(id: Long): Long? {
        return transaction {
            Store.select {
                Store.user eq id
            }.map { it[Store.expire] }.firstOrNull()
        }
    }

    fun addMonths(id: Long, months: Int) {
        val expire = getExpire(id)

        val newExpire by lazy {
            if(expire == null) System.currentTimeMillis() + TimeUnit.DAYS.toMillis(months * 30L)
            else expire + TimeUnit.DAYS.toMillis(months * 30L)
        }

        cache.removeAll { it.id == id }

        val user = PremiumUser(id, true, newExpire)
        cache.add(user)
        RedisBackend.PremiumUpdates.publishUpdate(user)

        transaction {
            Store.upsert(listOf(Store.expire)) {
                it[Store.user] = id
                it[Store.expire] = newExpire
            }
        }
    }
}