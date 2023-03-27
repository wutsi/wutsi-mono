package com.wutsi.application.membership.onboard.entity

import java.io.Serializable

data class OnboardEntity(
    val phoneNumber: String = "",
    var otpToken: String = "",
    var displayName: String = "",
    var pin: String = "",
    var language: String = "",
    var country: String = "",
) : Serializable
