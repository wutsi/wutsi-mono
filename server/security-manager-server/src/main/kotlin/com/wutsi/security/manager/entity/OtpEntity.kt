package com.wutsi.security.manager.entity

data class OtpEntity(
    val token: String = "",
    val code: String = "",
    val expires: Long = -1,
    val address: String = "",
) : java.io.Serializable
