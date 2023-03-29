package com.wutsi.event

@Deprecated("")
data class MemberEventPayload(
    val accountId: Long = -1,
    val phoneNumber: String = "",
    val pin: String? = null,
)
