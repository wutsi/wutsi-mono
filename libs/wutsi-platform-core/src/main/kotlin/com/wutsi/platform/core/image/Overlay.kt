package com.wutsi.platform.core.image

data class Overlay(
    val type: OverlayType,
    val input: String,
    val dimension: Dimension? = null,
)
