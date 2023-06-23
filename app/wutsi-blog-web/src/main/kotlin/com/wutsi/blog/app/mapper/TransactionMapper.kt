package com.wutsi.blog.app.mapper

import com.wutsi.blog.app.model.TransactionModel
import com.wutsi.blog.app.model.UserModel
import com.wutsi.blog.app.model.WalletModel
import com.wutsi.blog.transaction.dto.Transaction
import org.springframework.stereotype.Service

@Service
class TransactionMapper {
    fun toTransactionModel(tx: Transaction, wallet: WalletModel, merchant: UserModel) = TransactionModel(
        id = tx.id,
        status = tx.status.name,
        wallet = wallet,
        merchant = merchant,
    )
}
