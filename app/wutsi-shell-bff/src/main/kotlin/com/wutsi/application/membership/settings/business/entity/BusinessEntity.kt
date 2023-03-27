package com.wutsi.application.membership.settings.business.entity

data class BusinessEntity(
    var displayName: String = "",
    var biography: String? = null,
    var categoryId: Long? = null,
    var cityId: Long? = null,
    var whatsapp: Boolean = false,
    var email: String = "",
    var otpToken: String = "",
) : java.io.Serializable
