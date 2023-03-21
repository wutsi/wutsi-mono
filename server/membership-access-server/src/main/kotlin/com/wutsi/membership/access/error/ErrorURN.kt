package com.wutsi.membership.access.error

enum class ErrorURN(val urn: String) {
    ACCOUNT_NOT_FOUND("urn:wutsi:error:membership-access:account-not-found"),
    ACCOUNT_SUSPENDED("urn:wutsi:error:membership-access:account-suspended"),

    ATTRIBUTE_NOT_VALID("urn:wutsi:error:membership-access:attribute-not-valid"),

    CATEGORY_NOT_FOUND("urn:wutsi:error:membership-access:category-not-found"),

    DEVICE_NOT_FOUND("urn:wutsi:error:membership-access:device-not-found"),

    NAME_ALREADY_ASSIGNED("urn:wutsi:error:membership-access:name-already-assigned"),

    PHONE_NUMBER_ALREADY_ASSIGNED("urn:wutsi:error:membership-access:phone-number-already-assigned"),

    PLACE_NOT_FOUND("urn:wutsi:error:membership-access:place-not-found"),
    PLACE_FEED_NOT_FOUND("urn:wutsi:error:membership-access:place-feed-not-found"),
}
