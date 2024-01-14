package com.wutsi.blog.app.model

data class MoneyModel(
    val value: Long = 0,
    val currency: String = "",
    val text: String = "",
) {
    val free: Boolean
        get() = (value == 0L)

    override fun toString(): String {
        return text
    }
}
