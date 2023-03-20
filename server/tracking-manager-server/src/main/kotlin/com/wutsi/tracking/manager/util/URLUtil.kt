package com.wutsi.tracking.manager.util

import java.net.URL
import java.net.URLDecoder

object URLUtil {
    fun extractParams(url: String): Map<String, String?> {
        try {
            val params = LinkedHashMap<String, String>()
            val query = URL(url).query
            val pairs = query.split("&".toRegex())
                .dropLastWhile { it.isEmpty() }
                .toTypedArray()

            for (pair in pairs) {
                val idx = pair.indexOf("=")
                val name = URLDecoder.decode(pair.substring(0, idx), "UTF-8")
                val value = URLDecoder.decode(pair.substring(idx + 1), "UTF-8")
                params[name] = value
            }
            return params
        } catch (ex: Exception) {
            return emptyMap()
        }
    }
}
