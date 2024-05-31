package com.wutsi.blog.kpi.dto

enum class KpiType {
    NONE,
    READ,
    SCROLL,
    SUBSCRIPTION,
    DURATION,
    CLICK,
    READER,
    READER_EMAIL,
    CLICK_RATE,
    LIKE,
    COMMENT,
    WPP_EARNING,
    WPP_BONUS,
    USER, // Number of user
    USER_BLOG, // Number of blogs
    USER_WPP, // Number of WPP member
    STORE, // Number of Stores
    PUBLICATION, // Number of publication
    PRODUCT, // Number of Product
    DONATION, // Number of donation
    DONATION_VALUE, // Total donation value
    SALES, // Number of sales
    SALES_VALUE, // Total sales value
    VIEW,
    IMPRESSION,
    TRANSACTION,
    TRANSACTION_SUCCESS,
    TRANSACTION_RATE,
}
