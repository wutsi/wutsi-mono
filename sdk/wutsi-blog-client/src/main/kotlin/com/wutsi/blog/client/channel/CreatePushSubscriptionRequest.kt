package com.wutsi.blog.client.channel

import jakarta.validation.constraints.NotBlank

data class CreatePushSubscriptionRequest(
    @get:NotBlank val token: String = "",
)
