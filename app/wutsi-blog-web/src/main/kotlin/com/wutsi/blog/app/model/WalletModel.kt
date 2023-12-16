package com.wutsi.blog.app.model

import com.wutsi.blog.app.util.NumberUtils

data class WalletModel(
    val id: String = "",
    val userId: Long = -1,
    val currency: String = "",
    val balance: MoneyModel = MoneyModel(),
    val country: CountryModel = CountryModel(),
    val donationCount: Long = 0,
    val chargeCount: Long = 0,
    val account: WalletAccountModel? = null,
    val lastCashoutDateText: String? = null,
    val nextCashoutDateText: String? = null,
) {
    val balanceText: String
        get() = NumberUtils.toHumanReadable(balance.value)
    val donationCountText: String
        get() = NumberUtils.toHumanReadable(donationCount)
}
