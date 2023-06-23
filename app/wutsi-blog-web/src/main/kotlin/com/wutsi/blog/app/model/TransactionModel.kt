package com.wutsi.blog.app.model

data class TransactionModel(
    val id: String = "",
    val status: String = "",
    val wallet: WalletModel = WalletModel(),
    val merchant: UserModel = UserModel(),
)
