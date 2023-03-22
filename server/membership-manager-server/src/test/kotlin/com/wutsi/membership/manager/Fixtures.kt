package com.wutsi.membership.manager

import com.wutsi.checkout.access.dto.PaymentProviderSummary
import com.wutsi.enums.AccountStatus
import com.wutsi.enums.PaymentMethodType
import com.wutsi.membership.access.dto.Account
import com.wutsi.membership.access.dto.AccountSummary
import com.wutsi.membership.access.dto.Category
import com.wutsi.membership.access.dto.CategorySummary
import com.wutsi.membership.access.dto.Phone
import com.wutsi.membership.access.dto.Place
import com.wutsi.membership.access.dto.PlaceSummary

object Fixtures {
    fun createAccountSummary() = AccountSummary(
        id = -1,
        displayName = "Ray Sponsible",
        language = "en",
        cityId = 1111L,
        country = "CM",
        business = false,
        categoryId = 2222L,
        status = AccountStatus.ACTIVE.name,
        pictureUrl = "https://www.img.com/100.png",
        name = "ray.sponsible",
    )

    fun createAccount(
        id: Long = -1,
        status: AccountStatus = AccountStatus.ACTIVE,
        business: Boolean = false,
        storeId: Long? = null,
        businessId: Long? = null,
        country: String = "CM",
        name: String? = null,
        phoneNumber: String = "",
    ) = Account(
        id = id,
        status = status.name,
        name = name,
        business = business,
        storeId = storeId,
        businessId = businessId,
        country = country,
        email = "ray.sponsible@gmail.com",
        displayName = "Ray Sponsible",
        language = "en",
        pictureUrl = "https://www.img.com/100.png",
        city = Place(
            id = 111,
            name = "Yaounde",
            longName = "Yaounde, Cameroun",
        ),
        category = Category(
            id = 555,
            title = "Ads",
        ),
        phone = Phone(
            id = id,
            number = phoneNumber,
            country = country,
        ),
    )

    fun createPlaceSummary(id: Long = -1, name: String = "Yaounde") = PlaceSummary(
        id = id,
        name = name,
    )

    fun createCategorySummary(id: Long = -1, title: String = "Foo") = CategorySummary(
        id = id,
        title = title,
    )

    fun createPaymentProviderSummary(
        id: Long = System.currentTimeMillis(),
        type: PaymentMethodType = PaymentMethodType.MOBILE_MONEY,
        code: String = "MTN",
    ) = PaymentProviderSummary(
        id = id,
        code = code,
        type = type.name,
        logoUrl = "https://prod-wutsi.s3.amazonaws.com/static/wutsi-assets/images/payment-providers/mtn.png",
    )
}
