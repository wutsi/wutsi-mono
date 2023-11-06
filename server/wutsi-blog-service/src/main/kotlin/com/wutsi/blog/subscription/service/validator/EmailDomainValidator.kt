package com.wutsi.blog.subscription.service.validator

import com.wutsi.blog.subscription.service.EmailValidator

class EmailDomainValidator(private val domains: List<String>) : EmailValidator {
    override fun validate(email: String): Boolean {
        val i = email.indexOf("@")
        if (i < 0) {
            return true
        }

        val domain = email.substring(i + 1).lowercase()
        return domains.contains(domain)
    }
}
