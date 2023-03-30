package com.wutsi.marketplace.access.error

enum class ErrorURN(val urn: String) {
    ATTRIBUTE_NOT_VALID("urn:wutsi:error:marketplace-access:attribute-not-valid"),

    CATEGORY_NOT_FOUND("urn:wutsi:error:marketplace-access:category-not-found"),
    PARENT_CATEGORY_NOT_FOUND("urn:wutsi:error:marketplace-access:parent-category-not-found"),

    DISCOUNT_NOT_FOUND("urn:wutsi:error:marketplace-access:discount-not-found"),
    DISCOUNT_DELETED("urn:wutsi:error:marketplace-access:discount-deleted"),

    FILE_NOT_FOUND("urn:wutsi:error:marketplace-access:file-not-found"),
    FILE_DELETED("urn:wutsi:error:marketplace-access:file-deleted"),

    MEETING_PROVIDER_NOT_FOUND("urn:wutsi:error:marketplace-access:meeting-provider-not-found"),

    PICTURE_NOT_FOUND("urn:wutsi:error:marketplace-access:picture-not-found"),
    PICTURE_DELETED("urn:wutsi:error:marketplace-access:picture-deleted"),

    PRICE_NOT_FOUND("urn:wutsi:error:marketplace-access:price-not-found"),
    PRODUCT_NOT_FOUND("urn:wutsi:error:marketplace-access:product-not-found"),
    PRODUCT_NOT_PUBLISHED("urn:wutsi:error:marketplace-access:product-not-published"),
    PRODUCT_DELETED("urn:wutsi:error:marketplace-access:product-deleted"),
    PRODUCT_NOT_AVAILABLE("urn:wutsi:error:marketplace-access:product-not-available"),

    RESERVATION_NOT_FOUND("urn:wutsi:error:marketplace-access:reservation-not-found"),

    STATUS_NOT_VALID("urn:wutsi:error:marketplace-access:status-not-valid"),

    STORE_NOT_FOUND("urn:wutsi:error:marketplace-access:store-not-found"),

    FUNDRAISING_NOT_FOUND("urn:wutsi:error:marketplace-access:fundraising-not-found"),
}
