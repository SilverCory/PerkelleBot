package com.perkelle.dev.bot.datastores

import org.jetbrains.exposed.sql.Table

interface DataStore {

    fun getTable(): Table
}