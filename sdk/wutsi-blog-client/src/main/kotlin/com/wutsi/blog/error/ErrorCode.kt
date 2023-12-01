package com.wutsi.blog.error

object ErrorCode {
    const val USER_SUSPENDED: String = "urn:wutsi:blog:error:user-suspended"
    const val USER_NOT_FOUND: String = "urn:wutsi:blog:error:user-not-found"
    const val USER_NAME_DUPLICATE: String = "urn:wutsi:blog:error:user-name-duplicate"
    const val USER_EMAIL_DUPLICATE: String = "urn:wutsi:blog:error:user-email-duplicate"

    const val SESSION_EXPIRED: String = "urn:wutsi:blog:error:session-expired"
    const val SESSION_NOT_FOUND: String = "urn:wutsi:blog:error:session-not-found"

    const val LINK_NOT_FOUND = "urn:wutsi:blog:error:link-not-found"
    const val LINK_EXPIRED = "urn:wutsi:blog:error:link-expired"

    const val STORY_NOT_FOUND: String = "urn:wutsi:blog:error:story-not-found"
    const val STORY_ALREADY_IMPORTED: String = "urn:wutsi:blog:error:story-already-imported"
    const val STORY_IMPORT_FAILED: String = "urn:wutsi:blog:error:story-import-failed"
    const val STORY_WITHOUT_CONTENT: String = "urn:wutsi:blog:error:story-without-content"

    const val PERMISSION_DENIED: String = "urn:wutsi:blog:error:permission-denied"
    const val PHONE_NUMBER_NOT_VALID: String = "urn:wutsi:blog:error:phone-number-not-valid"

    const val TRANSACTION_NOT_FOUND: String = "urn:wutsi:blog:error:transaction-not-found"

    const val PRODUCT_ID_MISSING: String = "urn:wutsi:blog:error:product-id-missing"
    const val PRODUCT_IMPORT_FAILED: String = "urn:wutsi:blog:error:product-import-failed"
    const val PRODUCT_FILE_LINK_MISSING: String = "urn:wutsi:blog:error:product-file-link-missing"
    const val PRODUCT_FILE_LINK_INVALID: String = "urn:wutsi:blog:error:product-file-link-invalid"
    const val PRODUCT_FILE_LINK_UNABLE_TO_DOWNLOAD: String = "urn:wutsi:blog:error:product-file-link-unable-to-download"
    const val PRODUCT_IMAGE_LINK_MISSING: String = "urn:wutsi:blog:error:product-image-link-missing"
    const val PRODUCT_IMAGE_LINK_INVALID: String = "urn:wutsi:blog:error:product-image-link-invalid"
    const val PRODUCT_IMAGE_LINK_UNABLE_TO_DOWNLOAD: String =
        "urn:wutsi:blog:error:product-image-link-unable-to-download"
    const val PRODUCT_PRICE_MISSING: String = "urn:wutsi:blog:error:product-price-missing"
    const val PRODUCT_PRICE_INVALID: String = "urn:wutsi:blog:error:product-price-invalid"
    const val PRODUCT_PRICE_ZERO: String = "urn:wutsi:blog:error:product-price-zero"
    const val PRODUCT_NOT_FOUND: String = "urn:wutsi:blog:error:product-not-found"
    const val PRODUCT_TITLE_MISSING: String = "urn:wutsi:blog:error:product-title-missing"

    const val USER_DONT_SUPPORT_WALLET: String = "urn:wutsi:blog:error:user-dont-support-wallet"
    const val COUNTRY_DONT_SUPPORT_WALLET: String = "urn:wutsi:blog:error:country-dont-support-wallet"
    const val WALLET_ALREADY_CREATED: String = "urn:wutsi:blog:error:wallet-already-created"
    const val WALLET_NOT_FOUND: String = "urn:wutsi:blog:error:wallet-not-found"
    const val WALLET_ACCOUNT_NUMNER_INVALID: String = "urn:wutsi:blog:error:wallet-account-number-invalid"
}
