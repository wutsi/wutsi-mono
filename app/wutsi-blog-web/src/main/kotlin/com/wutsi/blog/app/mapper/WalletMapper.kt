package com.wutsi.blog.app.mapper

import com.wutsi.blog.app.model.MoneyModel
import com.wutsi.blog.app.model.WalletModel
import com.wutsi.blog.country.dto.Country
import com.wutsi.blog.transaction.dto.Wallet
import org.springframework.stereotype.Service

@Service
class WalletMapper(private val countryMapper: CountryMapper) {
    fun toWalletModel(wallet: Wallet): WalletModel {
        val country = Country.all.find { it.code == wallet.country }!!
        return WalletModel(
            id = wallet.id,
            userId = wallet.userId,
            currency = wallet.currency,
            country = countryMapper.toCountryModel(country),
            donationCount = wallet.donationCount,
            balance = MoneyModel(
                value = wallet.balance,
                currency = wallet.currency,
                text = country.createMoneyFormat().format(wallet.balance),
            ),
        )
    }
}
