package com.perkelle.dev.bot.wrappers

import org.json.JSONArray
import org.json.JSONObject

inline fun json(startingCode: String = "{}", block: JSONBuilder.() -> Unit) = JSONBuilder(startingCode).also { block(it) }.getJSON()

class JSONBuilder(startingCode: String) {

    private val json = JSONObject(startingCode)

    infix fun String.put(value: Any) = json.put(this, value)
    infix fun String.put(value: List<String>) = this.put(value.toTypedArray())
    infix fun String.put(value: Array<String>) = json.put(this, JSONArray(value))

    fun getJSON() = json.toString()
}
