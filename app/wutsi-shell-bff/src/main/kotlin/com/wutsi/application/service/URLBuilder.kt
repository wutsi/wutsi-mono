package com.wutsi.application.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class URLBuilder(
    @Value("\${wutsi.application.server-url}") private val serverUrl: String,
) {
    fun build(path: String) = build(serverUrl, path)

    private fun build(prefix: String, path: String): String {
        val xprefix = if (prefix.endsWith("/")) {
            prefix.substring(0, prefix.length - 1)
        } else {
            prefix
        }

        val xpath = if (path.startsWith("/")) {
            path.substring(1)
        } else {
            path
        }

        return if (xpath.isEmpty()) xprefix else "$xprefix/$xpath"
    }
}
