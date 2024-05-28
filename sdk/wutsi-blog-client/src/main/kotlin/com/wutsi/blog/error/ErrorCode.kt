package com.wutsi.blog.error

object ErrorCode {
    val ADS_ATTRIBUTE_INVALID: String = "urn:wutsi:blog:error:ads-attribute-invalid"
    val ADS_BUDGET_MISSING: String = "urn:wutsi:blog:error:ads-budget-missing"
    val ADS_END_DATE_BEFORE_START_DATE: String = "urn:wutsi:blog:error:ads-end-date-before-end-date"
    val ADS_END_DATE_MISSING: String = "urn:wutsi:blog:error:ads-end-date-missing"
    val ADS_IMAGE_URL_MISSING: String = "urn:wutsi:blog:error:ads-image-url-missing"
    val ADS_NOT_IN_DRAFT: String = "urn:wutsi:blog:error:ads-not-in-draft"
    val ADS_NOT_FOUND: String = "urn:wutsi:blog:error:ads-not-found"
    val ADS_START_DATE_MISSING: String = "urn:wutsi:blog:error:ads-start-date-missing"
    val ADS_URL_MISSING: String = "urn:wutsi:blog:error:ads-url-missing"
    val ADS_PAYMENT_MISSING: String = "urn:wutsi:blog:error:ads-payment-missing"

    val BOOK_NOT_FOUND: String = "urn:wutsi:blog:error:book-not-found"

    val CATEGORY_NOT_FOUND: String = "urn:wutsi:blog:error:category-not-found"
    val CATEGORY_PARENT_NOT_FOUND: String = "urn:wutsi:blog:error:category-parent-not-found"

    val COUPON_NOT_FOUND: String = "urn:wutsi:blog:error:coupon-not-found"
    val COUPON_ALREADY_USED: String = "urn:wutsi:blog:error:coupon-already-used"
    val COUPON_EXPIRED: String = "urn:wutsi:blog:error:coupon-expired"
    val COUPON_PRODUCT_MISMATCH: String = "urn:wutsi:blog:error:coupon-product-mismatch"
    val COUPON_USER_MISMATCH: String = "urn:wutsi:blog:error:coupon-user-mismatch"

    val USER_SUSPENDED: String = "urn:wutsi:blog:error:user-suspended"
    val USER_NOT_FOUND: String = "urn:wutsi:blog:error:user-not-found"
    val USER_NAME_DUPLICATE: String = "urn:wutsi:blog:error:user-name-duplicate"
    val USER_EMAIL_DUPLICATE: String = "urn:wutsi:blog:error:user-email-duplicate"
    val USER_NOT_BLOG: String = "urn:wutsi:blog:error:user-not-a-blog"
    val USER_DONT_SUPPORT_WALLET: String = "urn:wutsi:blog:error:user-dont-support-wallet"
    val USER_DONT_SUPPORT_STORE: String = "urn:wutsi:blog:error:user-dont-support-store"
    val USER_HAS_NO_STORE: String = "urn:wutsi:blog:error:user-has-no-store"
    val USER_HAS_NO_WALLET: String = "urn:wutsi:blog:error:user-has-no-wallet"
    val USER_NOT_WPP_MEMBER: String = "urn:wutsi:blog:error:user-not-wpp-member"
    val USER_ATTRIBUTE_INVALID: String = "urn:wutsi:blog:error:user-attribute-invalid"

    val SESSION_EXPIRED: String = "urn:wutsi:blog:error:session-expired"
    val SESSION_NOT_FOUND: String = "urn:wutsi:blog:error:session-not-found"

    val LINK_NOT_FOUND = "urn:wutsi:blog:error:link-not-found"
    val LINK_EXPIRED = "urn:wutsi:blog:error:link-expired"

    val PRODUCT_ATTRIBUTE_INVALID: String = "urn:wutsi:blog:error:product-attribute-invalid"

    val STORY_NOT_FOUND: String = "urn:wutsi:blog:error:story-not-found"
    val STORY_ALREADY_IMPORTED: String = "urn:wutsi:blog:error:story-already-imported"
    val STORY_IMPORT_FAILED: String = "urn:wutsi:blog:error:story-import-failed"
    val STORY_WITHOUT_CONTENT: String = "urn:wutsi:blog:error:story-without-content"

    val PERMISSION_DENIED: String = "urn:wutsi:blog:error:permission-denied"
    val PHONE_NUMBER_NOT_VALID: String = "urn:wutsi:blog:error:phone-number-not-valid"

    val TRANSACTION_NOT_FOUND: String = "urn:wutsi:blog:error:transaction-not-found"

    val PRODUCT_CATEGORY_INVALID: String = "urn:wutsi:blog:error:product-category-invalid"
    val PRODUCT_CATEGORY_MISSING: String = "urn:wutsi:blog:error:product-category-missing"
    val PRODUCT_IMPORT_FAILED: String = "urn:wutsi:blog:error:product-import-failed"
    val PRODUCT_ID_MISSING: String = "urn:wutsi:blog:error:product-id-missing"
    val PRODUCT_TITLE_MISSING: String = "urn:wutsi:blog:error:product-title-missing"
    val PRODUCT_IMAGE_LINK_MISSING: String = "urn:wutsi:blog:error:product-image-link-missing"
    val PRODUCT_IMAGE_LINK_INVALID: String = "urn:wutsi:blog:error:product-image-link-invalid"
    val PRODUCT_IMAGE_LINK_UNABLE_TO_DOWNLOAD: String = "urn:wutsi:blog:error:product-image-link-unable-to-download"
    val PRODUCT_FILE_LINK_MISSING: String = "urn:wutsi:blog:error:product-file-link-missing"
    val PRODUCT_FILE_LINK_INVALID: String = "urn:wutsi:blog:error:product-file-link-invalid"
    val PRODUCT_FILE_LINK_UNABLE_TO_DOWNLOAD: String = "urn:wutsi:blog:error:product-file-link-unable-to-download"
    val PRODUCT_PRICE_MISSING: String = "urn:wutsi:blog:error:product-price-missing"
    val PRODUCT_PRICE_INVALID: String = "urn:wutsi:blog:error:product-price-invalid"
    val PRODUCT_PRICE_ZERO: String = "urn:wutsi:blog:error:product-price-zero"
    val PRODUCT_NOT_FOUND: String = "urn:wutsi:blog:error:product-not-found"
    val PRODUCT_LIRETAMA_URL_NOT_VALID: String = "urn:wutsi:blog:error:product-liretama-url-not-valid"

    val STORE_NOT_FOUND: String = "urn:wutsi:blog:error:store-not-found"

    val COUNTRY_DONT_SUPPORT_WALLET: String = "urn:wutsi:blog:error:country-dont-support-wallet"
    val WALLET_ALREADY_CREATED: String = "urn:wutsi:blog:error:wallet-already-created"
    val WALLET_NOT_FOUND: String = "urn:wutsi:blog:error:wallet-not-found"
    val WALLET_ACCOUNT_NUMNER_INVALID: String = "urn:wutsi:blog:error:wallet-account-number-invalid"
}
