package com.wutsi.blog.client.story

import com.wutsi.blog.client.story.StoryAccess.PUBLIC
import java.util.Date
import javax.validation.constraints.Future
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

data class PublishStoryRequest(
    @get:NotBlank val title: String = "",
    @get:NotBlank val summary: String = "",
    @get:NotNull val topidId: Long? = null,
    @get:NotEmpty val tags: List<String> = emptyList(),
    val tagline: String? = null,
    val publishToSocialMedia: Boolean? = null,
    val socialMediaMessage: String? = null,
    @get:Future val scheduledPublishDateTime: Date? = null,
    val access: StoryAccess = PUBLIC,
)
