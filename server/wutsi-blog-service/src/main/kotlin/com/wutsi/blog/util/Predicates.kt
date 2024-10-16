package com.wutsi.blog.util

import jakarta.persistence.Query

object Predicates {
    fun or(vararg statements: String?): String? {
        val conditions = statements.filter { it != null }
        if (conditions.isEmpty()) {
            return null
        } else if (conditions.size == 1) {
            return conditions[0]
        } else {
            return "(" + conditions.joinToString(separator = " OR ") + ")"
        }
    }

    fun not(vararg statements: String?): String? {
        val conditions = statements.filter { it != null }
        if (conditions.isEmpty()) {
            return null
        } else {
            return "NOT ($conditions)"
        }
    }

    fun `in`(name: String, values: Collection<Any>?): String? {
        if (values == null || values.isEmpty()) {
            return null
        }

        val xvalues = mutableListOf<Any>()
        for (value in values) {
            xvalues.add(value)
        }

        if (xvalues.size == 1) {
            return eq(name, values.iterator().next())
        } else {
            val sb = StringBuilder()
            for (i in xvalues.indices) {
                if (i > 0) {
                    sb.append(',')
                }
                sb.append('?')
            }
            return String.format("%s IN (%s)", name, sb.toString())
        }
    }

    fun notIn(name: String, values: Collection<Any>?): String? {
        if (values == null || values.isEmpty()) {
            return null
        }

        val xvalues = mutableListOf<Any>()
        for (value in values) {
            xvalues.add(value)
        }

        if (xvalues.size == 1) {
            return notEq(name, values.iterator().next())
        } else {
            val sb = StringBuilder()
            for (i in xvalues.indices) {
                if (i > 0) {
                    sb.append(',')
                }
                sb.append('?')
            }
            return String.format("%s NOT IN (%s)", name, sb.toString())
        }
    }

    fun eq(name: String, value: Any?): String? = if (value == null) null else String.format("%s = ?", name)

    fun notEq(name: String, value: Any?): String? = if (value == null) null else String.format("%s <> ?", name)

    fun like(name: String, value: String?): String? =
        if (value == null || value.isEmpty()) null else String.format("%s LIKE ?", name)

    fun lt(name: String, value: Any?): String? = if (value == null) null else String.format("%s < ?", name)

    fun lte(name: String, value: Any?): String? = if (value == null) null else String.format("%s <= ?", name)

    fun gt(name: String, value: Any?): String? = if (value == null) null else String.format("%s > ?", name)

    fun gte(name: String, value: Any?): String? = if (value == null) null else String.format("%s >= ?", name)

    fun `null`(name: String): String = String.format("%s is null", name)

    fun between(name: String, min: Any?, max: Any?): String? {
        if (min == null && max == null) {
            return null
        } else if (max == null) {
            return gte(name, min)
        } else if (min == null) {
            return lte(name, max)
        } else {
            return "$name BETWEEN ? AND ?"
        }
    }

    fun where(predicates: Collection<String?>): String {
        val conditions = predicates.filter { it != null }.joinToString(separator = " AND ")
        return if (conditions.length > 0) " WHERE $conditions" else ""
    }

    fun parameters(vararg values: Any?): Array<Any> {
        val params = mutableListOf<Any>()
        for (value in values) {
            if (value == null) {
                continue
            }

            if (value is Collection<*>) {
                for (`val` in value) {
                    appendParameter(`val`, params)
                }
            } else if (value is Enum<*>) {
                appendParameter(value.ordinal, params)
            } else {
                appendParameter(value, params)
            }
        }
        return params.toTypedArray()
    }

    private fun appendParameter(value: Any?, params: MutableList<Any>) {
        if (value == null) {
            return
        } else if (value is Enum<*>) {
            params.add(value.name)
        } else {
            params.add(value)
        }
    }

    fun setParameters(query: Query, parameters: Array<Any>) {
        for (i in parameters.indices) {
            query.setParameter(i + 1, parameters[i])
        }
    }
}
