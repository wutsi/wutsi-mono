package com.wutsi.event

data class MemberEventPayload(
    val accountId: Long = -1,
    val phoneNumber: String = "",
    val pin: String? = null
)
