package com.wutsi.blog.app.mapper

import com.wutsi.blog.app.model.MoneyModel
import com.wutsi.blog.app.model.TransactionModel
import com.wutsi.blog.app.model.UserModel
import com.wutsi.blog.app.model.WalletModel
import com.wutsi.blog.app.service.Moment
import com.wutsi.blog.country.dto.Country
import com.wutsi.blog.transaction.dto.Transaction
import com.wutsi.blog.transaction.dto.TransactionSummary
import org.springframework.stereotype.Service
import java.text.DecimalFormat
import java.text.NumberFormat

@Service
class TransactionMapper(private val moment: Moment) {
    fun toTransactionModel(tx: Transaction, wallet: WalletModel, merchant: UserModel): TransactionModel {
        val country = Country.all.find { it.code == wallet.country.code }
        val fmt = country?.createMoneyFormat() ?: DecimalFormat("#,###,##0")
        return TransactionModel(
            id = tx.id,
            status = tx.status,
            type = tx.type,
            paymentMethodType = tx.paymentMethodType,
            paymentMethodOwner = if (tx.anonymous) {
                ""
            } else {
                tx.paymentMethodOwner
            },
            wallet = wallet,
            merchant = merchant,
            amount = toMoneyModel(tx.amount, tx.currency, fmt),
            fees = toMoneyModel(tx.fees, tx.currency, fmt),
            net = toMoneyModel(tx.net, tx.currency, fmt),
            creationDateTimeText = moment.format(tx.creationDateTime),
        )
    }

    fun toTransactionModel(tx: TransactionSummary, wallet: WalletModel, merchant: UserModel): TransactionModel {
        val country = Country.all.find { it.code == wallet.country.code }
        val fmt = country?.createMoneyFormat() ?: DecimalFormat("#,###,##0")
        return TransactionModel(
            id = tx.id,
            status = tx.status,
            type = tx.type,
            paymentMethodType = tx.paymentMethodType,
            paymentMethodOwner = if (tx.anonymous) {
                ""
            } else {
                tx.paymentMethodOwner
            },
            wallet = wallet,
            merchant = merchant,
            amount = toMoneyModel(tx.amount, tx.currency, fmt),
            fees = toMoneyModel(tx.fees, tx.currency, fmt),
            net = toMoneyModel(tx.net, tx.currency, fmt),
            creationDateTimeText = moment.format(tx.creationDateTime),
        )
    }

    private fun toMoneyModel(value: Long, currency: String, fmt: NumberFormat) =
        MoneyModel(
            value = value,
            currency = currency,
            text = fmt.format(value),
        )
}
