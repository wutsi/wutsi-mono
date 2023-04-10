package com.wutsi.error

enum class ErrorURN(val urn: String) {
    BUSINESS_ACCOUNT_NOT_SUPPORTED_IN_COUNTRY("urn:wutsi:error:business-account-not-supported-in-country"),
    BUSINESS_NOT_ACTIVE("urn:wutsi:error:business-not-active"),

    DISCOUNT_INVALID_DATE("urn:wutsi:error:discount-invalid-date"),

    FUNDRAISING_NOT_FOUND("urn:wutsi:error:fundraising-not-found"),
    FUNDRAISING_NOT_SUPPORTED_IN_COUNTRY("urn:wutsi:error:fundraising-not-supported-in-country"),
    FUNDRAISING_NOT_ACTIVE("urn:wutsi:error:fundraising-not-active"),

    IDEMPOTENCY_KEY_NOT_VALID("urn:wutsi:error:idempotency-key-not-valid"),

    MEMBER_ALREADY_BUSINESS("urn:wutsi:error:member-already-business"),
    MEMBER_ALREADY_REGISTERED("urn:wutsi:error:member-already-registered"),
    MEMBER_NOT_ACTIVE("urn:wutsi:error:member-not-active"),
    MEMBER_NOT_BUSINESS("urn:wutsi:error:member-not-business-account"),
    MEMBER_NOT_FOUND("urn:wutsi:error:member-not-found"),

    NO_STORE("urn:wutsi:error:no-store"),
    NO_FUNDRAISING("urn:wutsi:error:no-fundraising"),

    ORDER_EXPIRED("urn:wutsi:error:order-expired"),
    ORDER_NOT_OWNER("urn:wutsi:error:order-not-owner"),
    ORDER_PRODUCT_NOT_FOUND("urn:wutsi:error:order-product-not-found"),

    PAYMENT_METHOD_NOT_ACTIVE("urn:wutsi:error:payment-method-not-active"),
    PAYMENT_METHOD_NOT_OWNER("urn:wutsi:error:payment-method-not-owner"),

    PHONE_NUMBER_ALREADY_ASSIGNED("urn:wutsi:error:phone-number-already-assigned"),

    PICTURE_LIMIT_REACHED("urn:wutsi:error:picture-limit-reached"),

    PRODUCT_LIMIT_REACHED("urn:wutsi:error:product-limit-reached"),
    PRODUCT_NOT_FOUND("urn:wutsi:error:product-not-found"),
    PRODUCT_NOT_PUBLISHED("urn:wutsi:error:product-not-published"),
    PRODUCT_NO_STOCK("urn:wutsi:error:product-no-stock"),
    PRODUCT_NOT_OWNER("urn:wutsi:error:product-not-owner"),
    PRODUCT_PICTURE_MISSING("urn:wutsi:error:product-pictures-missing"),
    PRODUCT_NOT_AVAILABLE("urn:wutsi:error:product-not-available"),
    PRODUCT_DIGITAL_DOWNLOAD_NO_FILE("urn:wutsi:error:product-digital-download-no-file"),
    PRODUCT_DIGITAL_DOWNLOAD_LIMIT_REACHED("urn:wutsi:error:product-digital-download-limit-reached"),
    PRODUCT_EVENT_NO_MEETING_ID("urn:wutsi:error:product-event-no-meeting-id"),
    PRODUCT_EVENT_NO_START_DATE("urn:wutsi:error:product-event-no-start-date"),
    PRODUCT_EVENT_NO_END_DATE("urn:wutsi:error:product-event-no-end-date"),
    PRODUCT_EVENT_START_DATE_IN_PAST("urn:wutsi:error:product-event-start-date-in-past"),
    PRODUCT_EVENT_END_DATE_BEFORE_START_DATE("urn:wutsi:error:product-event-end-date-before-start-date"),
    PRODUCT_EVENT_MEETING_ID_NOT_VALID("urn:wutsi:error:product-event-meeting-id-not-valid"),
    PRODUCT_FILE_NOT_FOUND("urn:wutsi:error:product-file-not-found"),
    PRODUCT_PRICE_MISSING("urn:wutsi:error:product-price-missing"),

    STORE_NOT_ACTIVE("urn:wutsi:error:store-not-active"),
    STORE_NOT_FOUND("urn:wutsi:error:store-not-found"),
    STORE_NOT_OWNER("urn:wutsi:error:store-not-owner"),
    STORE_NOT_SUPPORTED_IN_COUNTRY("urn:wutsi:error:store-not-supported-in-country"),

    TRANSACTION_FAILED("urn:wutsi:error:transaction-failed"),

    USERNAME_ALREADY_ASSIGNED("urn:wutsi:error:username-already-assigned"),
}
