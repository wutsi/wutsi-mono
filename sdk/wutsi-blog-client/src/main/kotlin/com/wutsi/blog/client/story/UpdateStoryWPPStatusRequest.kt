package com.wutsi.blog.client.story

import javax.validation.constraints.NotNull

data class UpdateStoryWPPStatusRequest(
    @get:NotNull val wppStatus: WPPStatus? = null,
    val wppRejectionReason: String? = null,
)
