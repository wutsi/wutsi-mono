package com.wutsi.blog.app.mapper

import com.wutsi.blog.app.model.MoneyModel
import com.wutsi.blog.app.model.WalletAccountModel
import com.wutsi.blog.app.model.WalletModel
import com.wutsi.blog.country.dto.Country
import com.wutsi.blog.transaction.dto.Wallet
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat

@Service
class WalletMapper(
    private val countryMapper: CountryMapper,
) {
    fun toWalletModel(wallet: Wallet): WalletModel {
        val country = Country.all.find { it.code.equals(wallet.country, true) }!!
        val fmt = SimpleDateFormat(country.dateFormat)
        return WalletModel(
            id = wallet.id,
            userId = wallet.userId,
            currency = wallet.currency,
            country = country.let { countryMapper.toCountryModel(country) },
            donationCount = wallet.donationCount,
            chargeCount = wallet.chargeCount,
            balance = MoneyModel(
                value = wallet.balance,
                currency = wallet.currency,
                text = country.createMoneyFormat().format(wallet.balance),
            ),
            lastCashoutDateText = wallet.lastCashoutDateTime?.let { fmt.format(it) },
            nextCashoutDateText = wallet.nextCashoutDate?.let { fmt.format(it) },
            account = wallet.account?.let {
                WalletAccountModel(
                    number = it.number?.ifEmpty { null },
                    type = it.type,
                    owner = it.owner?.ifEmpty { null },
                )
            },
        )
    }
}
