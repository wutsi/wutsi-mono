package com.wutsi.blog.client.user

import javax.validation.constraints.NotBlank

data class RunAsRequest(
    @get:NotBlank val accessToken: String? = null,
    @get:NotBlank val userName: String? = null,
)
