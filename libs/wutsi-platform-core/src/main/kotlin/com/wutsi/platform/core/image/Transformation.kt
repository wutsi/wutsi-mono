package com.wutsi.platform.core.image

data class Transformation(
    val dimension: Dimension? = null,
    val aspectRatio: AspectRatio? = null,
    val focus: Focus? = null,
    val format: Format? = null,
    val overlay: Overlay? = null,
)
