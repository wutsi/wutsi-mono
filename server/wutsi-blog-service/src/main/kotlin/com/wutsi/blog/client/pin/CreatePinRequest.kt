package com.wutsi.blog.client.pin

import javax.validation.constraints.NotNull

data class CreatePinRequest(
    @get:NotNull val storyId: Long? = null,
)
