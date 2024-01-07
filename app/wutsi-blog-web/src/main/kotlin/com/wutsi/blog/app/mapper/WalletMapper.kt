package com.wutsi.blog.app.mapper

import com.wutsi.blog.app.model.MoneyModel
import com.wutsi.blog.app.model.WalletAccountModel
import com.wutsi.blog.app.model.WalletModel
import com.wutsi.blog.app.service.Moment
import com.wutsi.blog.country.dto.Country
import com.wutsi.blog.transaction.dto.PaymentMethodType
import com.wutsi.blog.transaction.dto.Wallet
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class WalletMapper(
    private val countryMapper: CountryMapper,
    private val moment: Moment,
    @Value("\${wutsi.application.asset-url}") private val assetUrl: String,
) {
    fun toWalletModel(wallet: Wallet): WalletModel {
        val country = Country.all.find { it.code.equals(wallet.country, true) }!!
        return WalletModel(
            id = wallet.id,
            userId = wallet.userId,
            currency = wallet.currency,
            country = countryMapper.toCountryModel(country),
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
                    providerLogoUrl = getProviderLogo(wallet, country),
                )
            },
        )
    }

    private fun getProviderLogo(wallet: Wallet, country: Country): String? {
        val account = wallet.account
        if (account == null || account.number.isNullOrEmpty()) {
            return null
        }

        if (account.type == PaymentMethodType.MOBILE_MONEY) {
            val prefix = country.phoneNumberPrefixes.find { account.number!!.startsWith(it.prefix) }
            return prefix?.let {
                "$assetUrl/assets/wutsi/img/payment/" + prefix.carrier.name.lowercase() + ".png"
            }
        }
        return null
    }
}
