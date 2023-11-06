package com.wutsi.blog.subscription.service.validator

import com.wutsi.blog.subscription.service.EmailValidator
import jakarta.mail.internet.InternetAddress

class EmailFormatValidator : EmailValidator {
    override fun validate(email: String): Boolean =
        try {
            val emailAddr = InternetAddress(email)
            emailAddr.validate()
            true
        } catch (ex: Exception) {
            false
        }
}
