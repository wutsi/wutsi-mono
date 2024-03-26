package com.wutsi.blog.app.mapper

import com.wutsi.blog.app.model.CountryModel
import com.wutsi.blog.app.model.MoneyModel
import com.wutsi.blog.app.model.WalletAccountModel
import com.wutsi.blog.app.model.WalletModel
import com.wutsi.blog.app.service.Moment
import com.wutsi.blog.country.dto.Country
import com.wutsi.blog.transaction.dto.Wallet
import org.springframework.stereotype.Service

@Service
class WalletMapper(
    private val countryMapper: CountryMapper,
    private val moment: Moment,
) {
    fun toWalletModel(wallet: Wallet): WalletModel {
        val country = Country.all.find { it.code.equals(wallet.country, true) }!!
        return WalletModel(
            id = wallet.id,
            userId = wallet.userId,
            currency = wallet.currency,
            country = country.let { countryMapper.toCountryModel(country) } ?: CountryModel(),
            donationCount = wallet.donationCount,
            chargeCount = wallet.chargeCount,
            balance = MoneyModel(
                value = wallet.balance,
                currency = wallet.currency,
                text = country.createMoneyFormat().format(wallet.balance),
            ),
            lastCashoutDateText = wallet.lastCashoutDateTime?.let { moment.format(it) },
            nextCashoutDateText = wallet.nextCashoutDate?.let { moment.format(it) },
            account = wallet.account?.let {
                WalletAccountModel(
                    number = it.number,
                    type = it.type,
                    owner = it.owner,
                )
            },
        )
    }
}
