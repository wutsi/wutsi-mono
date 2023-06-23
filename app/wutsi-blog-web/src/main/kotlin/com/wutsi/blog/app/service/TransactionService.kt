package com.wutsi.blog.app.service

import com.wutsi.blog.app.backend.WalletBackend
import com.wutsi.blog.app.form.CreateWalletForm
import com.wutsi.blog.app.mapper.WalletMapper
import com.wutsi.blog.app.model.WalletModel
import com.wutsi.blog.country.dto.Country
import com.wutsi.blog.transaction.dto.CreateWalletCommand
import org.springframework.stereotype.Component

@Component
class WalletService(
    private val backend: WalletBackend,
    private val mapper: WalletMapper,
    private val requestContext: RequestContext,
) {
    fun create(form: CreateWalletForm): String {
        val country = findCountry(form.currency)
        return backend.create(
            CreateWalletCommand(
                userId = requestContext.currentUser()!!.id,
                country = country!!.code,
            ),
        ).walletId
    }

    fun get(walletId: String): WalletModel =
        mapper.toWalletModel(
            backend.get(walletId).wallet
        )

    private fun findCountry(code: String): Country? =
        Country.all.find { it.code == code }
}
