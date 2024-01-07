package com.wutsi.blog.app.form

data class DonateForm(
    val name: String = "",
    val fullName: String = "",
    val amount: Long = 0,
    val email: String = "",
    val number: String = "",
    val idempotencyKey: String = "",
    val country: String = "",
    val error: String? = null,
    val redirect: String? = null,
)
