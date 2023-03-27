package com.wutsi.application.membership.settings.security.entity

import java.io.Serializable

data class PasscodeEntity(
    val pin: String = "",
) : Serializable
