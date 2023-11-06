package com.wutsi.blog.subscription.service.validator

import com.wutsi.blog.subscription.service.EmailValidator

class EmailRoleValidator(private val roles: List<String>) : EmailValidator {
    override fun validate(email: String): Boolean {
        val i = email.indexOf("@")
        if (i < 0) {
            return true
        }

        val prefix = email.substring(0, i).lowercase()
        return !roles.contains(prefix)
    }
}
