package com.wutsi.blog.error

object ErrorCode {
    val USER_SUSPENDED: String = "urn:wutsi:blog:error:user-suspended"
    val USER_NOT_FOUND: String = "urn:wutsi:blog:error:user-not-found"
    val USER_NAME_DUPLICATE: String = "urn:wutsi:blog:error:user-name-duplicate"
    val USER_EMAIL_DUPLICATE: String = "urn:wutsi:blog:error:user-email-duplicate"

    val SESSION_EXPIRED: String = "urn:wutsi:blog:error:session-expired"
    val SESSION_NOT_FOUND: String = "urn:wutsi:blog:error:session-not-found"

    val LINK_NOT_FOUND = "urn:wutsi:blog:error:link-not-found"
    val LINK_EXPIRED = "urn:wutsi:blog:error:link-expired"

    val STORY_NOT_FOUND: String = "urn:wutsi:blog:error:story-not-found"
    val STORY_ALREADY_IMPORTED: String = "urn:wutsi:blog:error:story-already-imported"
    val STORY_IMPORT_FAILED: String = "urn:wutsi:blog:error:story-import-failed"
    val STORY_WITHOUT_CONTENT: String = "urn:wutsi:blog:error:story-without-content"

    val PERMISSION_DENIED: String = "urn:wutsi:blog:error:permission-denied"
    val PHONE_NUMBER_NOT_VALID: String = "urn:wutsi:blog:error:phone-number-not-valid"

    val TRANSACTION_NOT_FOUND: String = "urn:wutsi:blog:error:transaction-not-found"

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

    val STORE_NOT_FOUND: String = "urn:wutsi:blog:error:store-not-found"

    val USER_DONT_SUPPORT_WALLET: String = "urn:wutsi:blog:error:user-dont-support-wallet"
    val COUNTRY_DONT_SUPPORT_WALLET: String = "urn:wutsi:blog:error:country-dont-support-wallet"
    val WALLET_ALREADY_CREATED: String = "urn:wutsi:blog:error:wallet-already-created"
    val WALLET_NOT_FOUND: String = "urn:wutsi:blog:error:wallet-not-found"
    val WALLET_ACCOUNT_NUMNER_INVALID: String = "urn:wutsi:blog:error:wallet-account-number-invalid"
}
