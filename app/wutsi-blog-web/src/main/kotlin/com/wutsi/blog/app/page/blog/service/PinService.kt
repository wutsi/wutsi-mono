package com.wutsi.blog.app.page.blog.service

import com.wutsi.blog.app.backend.PinBackend
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.blog.model.PinModel
import com.wutsi.blog.app.page.settings.model.UserModel
import com.wutsi.blog.client.pin.CreatePinRequest
import org.springframework.stereotype.Service

@Service
class PinService(
    private val api: PinBackend,
    private val mapper: PinMapper,
    private val requestContext: RequestContext,
) {
    fun create(storyId: Long) {
        val user = requestContext.currentUser()
            ?: return

        api.create(user.id, CreatePinRequest(storyId = storyId))
    }

    fun delete() {
        val user = requestContext.currentUser()
            ?: return

        api.delete(user.id)
    }

    fun get(user: UserModel): PinModel? {
        try {
            val pin = api.get(user.id).pin
            return mapper.toPinModel(pin)
        } catch (ex: Exception) {
            return null
        }
    }
}
