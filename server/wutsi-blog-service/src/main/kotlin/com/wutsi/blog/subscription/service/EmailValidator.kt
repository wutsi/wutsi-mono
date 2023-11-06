package com.wutsi.blog.subscription.service

interface EmailValidator {
    fun validate(email: String): Boolean
}
