package com.wutsi.blog.subscription.service

class EmailValidatorSet(private val validators: List<EmailValidator>) {
    fun validate(email: String): String? {
        val validator = validators.find { !it.validate(email) }
        return validator?.javaClass?.simpleName
    }
}
