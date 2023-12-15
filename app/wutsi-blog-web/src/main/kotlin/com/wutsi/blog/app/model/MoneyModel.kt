package com.wutsi.blog.app.model

data class MoneyModel(
    val value: Long = 0,
    val currency: String = "",
    val text: String = "",
) {
    override fun toString(): String {
        return text
    }
}
