package com.wutsi.blog.ads.dto

enum class AdsType(
    val width: Int,
    val height: Int,
    val desktop: Boolean,
    val tablet: Boolean,
    val mobile: Boolean,
) {
    UNKNOWN(-1, -1, false, false, false),
    BANNER_WEB(728, 100, true, true, false),
    BANNER_MOBILE(300, 50, false, false, true),
    BOX(300, 300, true, true, true),
    BOX_2X(300, 600, true, true, true),
}
