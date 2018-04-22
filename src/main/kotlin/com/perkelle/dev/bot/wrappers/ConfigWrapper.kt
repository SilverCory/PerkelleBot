package com.perkelle.dev.bot.wrappers

import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.lang.StringBuilder
import java.util.regex.Matcher
import java.util.regex.Pattern

abstract class JSON(private val folder: File? = null) {

    private lateinit var cfile: File
    lateinit var config: JSONConfiguration

    @Throws(IOException::class)
    fun load() {
        if (folder != null && !folder.exists()) folder.mkdir()

        cfile = if (folder == null) File(javaClass.getAnnotation(FileName::class.java).fileName)
        else File(folder, javaClass.getAnnotation(FileName::class.java).fileName)

        val json = cfile.bufferedReader().readLines().joinToString(" ")
        config = JSONConfiguration(JSONObject(json))
    }
}

class JSONConfiguration(val obj: JSONObject) {

    fun getElement(path: String): Any {
        try {
            val parts = path.split(".")
            if (parts.size == 1) return get(parts[0])
            else {
                var cfg = obj
                for ((i, part) in parts.withIndex()) {
                    if (i == parts.size-1) return cfg.get(part)
                    else cfg = cfg.getJSONObject(part)
                }
            }
        } catch(_: JSONException) {}
        throw InvalidJSONPathException()
    }

    private fun get(key: String): Any = obj.get(key)

    inline fun <reified T> getGeneric(key: String, default: T): T {
        try {
            val generic = getElement(key)
            if (generic::class.java == T::class.java) return generic as T
        } catch(_: Exception) {}
        return default
    }

    inline fun <reified T> getGenericOrNull(key: String): T? {
        try {
            val generic = getElement(key)
            if (generic::class.java == T::class.java) return generic as T
        } catch(_: Exception) {}
        return null
    }

    fun getRawJSON(): String = obj.toString()

    fun set(key: String, data: Any) {
        val parts = key.split(".")
        var cfg = obj
        if(parts.size == 1) {
            obj.put(key, data)
            return
        }
        else for((i, part) in parts.subList(0, parts.size-1).withIndex()) {
            if(!cfg.has(part) && i != part.length-1) cfg.put(part, JSONObject())
            cfg = cfg.getJSONObject(part)
        }
        cfg.put(parts.last(), data)
    }

    fun contains(key: String): Boolean {
        return try {
            getElement(key)
            true
        } catch(_: InvalidJSONPathException) {
            false
        }
    }

    fun remove(key: String) {
        try {
            val parts = key.split(".")
            if (parts.size == 1) obj.remove(parts[0])
            else {
                var cfg = obj
                for ((i, part) in parts.withIndex()) {
                    if (i == parts.size-1) {
                        cfg.remove(part)
                        break
                    }
                    else cfg = cfg.getJSONObject(part)
                }
            }
        } catch(e: JSONException) {
            throw InvalidJSONPathException()
        }
    }
}

/**
 * @author https://github.com/getify/JSON.minify/blob/java/src/main/java/io/github/getify/minify/Minify.java
 */
class Minify {

    private val TOKENIZER = "\"|(/\\*)|(\\*/)|(//)|\\n|\\r"
    private val MAGIC = "(\\\\)*$"
    private val PATTERN = Pattern.compile(TOKENIZER)
    private val MAGIC_PATTERN = Pattern.compile(MAGIC)

    fun minify(json: CharSequence): String {
        val jsonString = json.toString()

        var in_string = false
        var in_multiline_comment = false
        var in_singleline_comment = false
        var tmp: String
        var tmp2: String
        val new_str = StringBuilder()
        var from: Int? = 0
        var lc: String
        var rc = ""

        val matcher = PATTERN.matcher(jsonString)

        var magicMatcher: Matcher
        var foundMagic: Boolean?

        if (!matcher.find())
            return jsonString
        else
            matcher.reset()

        while (matcher.find()) {
            lc = jsonString.substring(0, matcher.start())
            rc = jsonString.substring(matcher.end(), jsonString.length)
            tmp = jsonString.substring(matcher.start(), matcher.end())

            if (!in_multiline_comment && !in_singleline_comment) {
                tmp2 = lc.substring(from!!)
                if (!in_string)
                    tmp2 = tmp2.replace("(\\n|\\r|\\s)*".toRegex(), "")

                new_str.append(tmp2)
            }
            from = matcher.end()

            if (tmp[0] == '\"' && (!in_multiline_comment) && (!in_singleline_comment)) {
                magicMatcher = MAGIC_PATTERN.matcher(lc)
                foundMagic = magicMatcher.find()
                if ((!in_string) || !foundMagic || (magicMatcher.end() - magicMatcher.start()) % 2 == 0) {
                    in_string = (!in_string)
                }
                from--
                rc = jsonString.substring(from)
            }
            else if (tmp.startsWith("/*") && !in_string && !in_multiline_comment && !in_singleline_comment) in_multiline_comment = true
            else if (tmp.startsWith("*/") && !in_string && in_multiline_comment) in_multiline_comment = false
            else if (tmp.startsWith("//") && !in_string && !in_multiline_comment && !in_singleline_comment) in_singleline_comment = true
            else if ((tmp.startsWith("\n") || tmp.startsWith("\r")) && !in_string && !in_multiline_comment && in_singleline_comment) in_singleline_comment = false
            else if (!in_multiline_comment && !in_singleline_comment && !tmp.substring(0, 1).matches("\\n|\\r|\\s".toRegex())) new_str.append(tmp)
        }
        new_str.append(rc)

        return new_str.toString()
    }
}

class InvalidJSONPathException: Exception()

annotation class FileName(val fileName: String = "config.json")
