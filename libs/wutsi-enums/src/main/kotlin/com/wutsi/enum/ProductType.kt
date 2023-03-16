package com.wutsi.enums

enum class ProductType(val numeric: Boolean) {
    UNKNOWN(false),
    PHYSICAL_PRODUCT(false),
    EVENT(true),
    DIGITAL_DOWNLOAD(true),
    MEMBERSHIP(true),
}
