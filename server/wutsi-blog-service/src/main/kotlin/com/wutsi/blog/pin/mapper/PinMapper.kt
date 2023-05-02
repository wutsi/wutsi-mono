package com.wutsi.blog.pin.mapper

import com.wutsi.blog.client.pin.PinDto
import com.wutsi.blog.pin.domain.Pin
import org.springframework.stereotype.Service

@Service
class PinMapper {
    fun toPinDto(obj: Pin): PinDto =
        PinDto(
            id = obj.userId,
            storyId = obj.storyId,
            userId = obj.userId,
            creationDateTime = obj.creationDateTime,
        )
}
