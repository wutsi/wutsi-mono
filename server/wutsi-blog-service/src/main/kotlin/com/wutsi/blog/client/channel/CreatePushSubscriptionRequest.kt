package com.wutsi.blog.client.channel

import javax.validation.constraints.NotBlank

data class CreatePushSubscriptionRequest(
    @get:NotBlank val token: String = "",
)
