package com.wutsi.blog.app.model

data class SuperFanModel(
    val user: UserModel? = null,
    val value: MoneyModel = MoneyModel(),
    val transactionCount: Long = 0,
) {
    val displayName: String
        get() = if (user == null) {
            "-"
        } else if (user.fullName.isEmpty()) {
            user.name
        } else {
            user.fullName
        }
}
