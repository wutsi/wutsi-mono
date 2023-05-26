package com.wutsi.blog.pin.dto

data class SearchPinResponse(
    val pins: List<PinStory> = emptyList(),
)
