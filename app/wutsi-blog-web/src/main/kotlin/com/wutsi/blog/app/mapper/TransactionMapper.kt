package com.wutsi.blog.app.mapper

import com.wutsi.blog.app.model.MoneyModel
import com.wutsi.blog.app.model.ProductModel
import com.wutsi.blog.app.model.TransactionModel
import com.wutsi.blog.app.model.UserModel
import com.wutsi.blog.app.model.WalletModel
import com.wutsi.blog.app.service.Moment
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.country.dto.Country
import com.wutsi.blog.transaction.dto.Transaction
import com.wutsi.blog.transaction.dto.TransactionSummary
import org.springframework.stereotype.Service
import java.text.DecimalFormat
import java.text.NumberFormat

@Service
class TransactionMapper(
    private val moment: Moment,
    private val requestContext: RequestContext,
) {
    fun toTransactionModel(
        tx: Transaction,
        wallet: WalletModel?,
        merchant: UserModel?,
        product: ProductModel? = null,
    ): TransactionModel {
        val country = wallet?.let { w -> Country.all.find { it.code == w.country.code } }
            ?: Country.all.find { c -> c.currency == tx.currency }
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
            paymentMethodNumber = tx.paymentMethodNumber,
            wallet = wallet,
            merchant = merchant,
            amount = toMoneyModel(tx.amount, tx.currency, fmt),
            fees = toMoneyModel(tx.fees, tx.currency, fmt),
            net = toMoneyModel(tx.net, tx.currency, fmt),
            creationDateTimeText = moment.format(tx.creationDateTime),
            email = tx.email ?: "",
            errorCode = tx.errorCode,
            errorMessage = toErrorMessage(tx.errorCode),
            product = product,
            gatewayTransactionId = tx.gatewayTransactionId,
            gatewayType = tx.gatewayType,
            adsId = tx.adsId,
        )
    }

    fun toTransactionModel(
        tx: TransactionSummary,
        wallet: WalletModel,
        merchant: UserModel,
        product: ProductModel? = null,
    ): TransactionModel {
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
            paymentMethodNumber = tx.paymentMethodNumber,
            wallet = wallet,
            merchant = merchant,
            product = product,
            amount = toMoneyModel(tx.amount, tx.currency, fmt),
            fees = toMoneyModel(tx.fees, tx.currency, fmt),
            net = toMoneyModel(tx.net, tx.currency, fmt),
            creationDateTimeText = moment.format(tx.creationDateTime),
            errorCode = tx.errorCode,
            errorMessage = toErrorMessage(tx.errorCode),
            adsId = tx.adsId,
        )
    }

    private fun toMoneyModel(value: Long, currency: String, fmt: NumberFormat) =
        MoneyModel(
            value = value,
            currency = currency,
            text = fmt.format(value),
        )

    private fun toErrorMessage(code: String?): String =
        requestContext.getMessage("error.payment.$code", "error.unexpected")
}
