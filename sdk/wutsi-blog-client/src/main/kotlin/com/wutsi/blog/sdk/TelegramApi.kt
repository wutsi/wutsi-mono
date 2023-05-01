package com.wutsi.blog.sdk

import com.wutsi.blog.client.telegram.CheckBotAccessRequest
import com.wutsi.blog.client.telegram.CheckBotAccessResponse

interface TelegramApi {
    fun checkAccess(request: CheckBotAccessRequest): CheckBotAccessResponse
}
