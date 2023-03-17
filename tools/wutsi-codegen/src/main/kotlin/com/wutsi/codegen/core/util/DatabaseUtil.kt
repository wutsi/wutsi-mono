package com.wutsi.codegen.core.util

object DatabaseUtil {
    private val PREFIXES = listOf(
        "-manager",
        "-access",
        "-server",
    )

    fun toDatabaseName(name: String): String =
        CaseUtil.toSnakeCase(trimPrefix(name), "_").lowercase()

    private fun trimPrefix(name: String): String {
        PREFIXES.forEach {
            if (name.endsWith(it)) {
                return name.substring(0, name.length - it.length)
            }
        }
        return name
    }
}
