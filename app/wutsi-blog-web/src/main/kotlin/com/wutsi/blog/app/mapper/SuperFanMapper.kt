package com.wutsi.blog.app.mapper

import com.wutsi.blog.app.model.SuperFanModel
import com.wutsi.blog.app.model.UserModel
import com.wutsi.blog.app.model.WalletModel
import com.wutsi.blog.transaction.dto.SuperFanSummary
import org.springframework.stereotype.Service

@Service
class SuperFanMapper(
    private val moneyMapper: MoneyMapper,
) {
    fun toSuperFanModel(superFan: SuperFanSummary, user: UserModel, walletModel: WalletModel): SuperFanModel {
        return SuperFanModel(
            user = user,
            value = moneyMapper.toMoneyModel(
                amount = superFan.value,
                currency = walletModel.currency
            ),
            transactionCount = superFan.transactionCount
        )
    }
}
