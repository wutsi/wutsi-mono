package com.wutsi.blog.story.dto

data class ValidateStoryWPPEligibilityResponse(
    val validation: WPPValidation = WPPValidation(),
)
