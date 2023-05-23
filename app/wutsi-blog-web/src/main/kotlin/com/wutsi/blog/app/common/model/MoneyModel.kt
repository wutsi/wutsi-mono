package com.wutsi.blog.app.common.model

import java.text.DecimalFormat

class MoneyModel(
    val value: Long = 0L,
    val currency: String = "",
) {
    fun isFree(): Boolean =
        value == 0L

    override fun toString(): String {
        if (value == 0L) {
            return ""
        } else {
            val fmt = DecimalFormat()
            return fmt.format(value) + " " + currency
        }
    }
}
