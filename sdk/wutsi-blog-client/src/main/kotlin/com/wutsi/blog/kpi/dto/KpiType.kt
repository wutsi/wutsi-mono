package com.wutsi.blog.kpi.dto

enum class KpiType {
    NONE,
    READ,
    SCROLL,
    SUBSCRIPTION,
    DURATION,
    CLICK,
    READER, // All readers
    READER_EMAIL, // Email readers
}
