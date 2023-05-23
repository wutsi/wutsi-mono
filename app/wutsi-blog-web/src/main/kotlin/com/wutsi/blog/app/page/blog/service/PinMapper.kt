package com.wutsi.blog.app.page.blog.service

import com.wutsi.blog.app.page.blog.model.PinModel
import com.wutsi.blog.client.pin.PinDto
import org.springframework.stereotype.Service

@Service
class PinMapper {
    fun toPinModel(obj: PinDto) = PinModel(
        id = obj.id,
        storyId = obj.storyId,
        userId = obj.userId,
    )
}
