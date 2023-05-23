package com.wutsi.blog.app.component.like.service

import com.wutsi.blog.app.component.like.model.LikeCountModel
import com.wutsi.blog.app.component.like.model.LikeModel
import com.wutsi.blog.app.util.NumberUtils
import com.wutsi.blog.client.like.LikeCountDto
import com.wutsi.blog.client.like.LikeDto
import org.springframework.stereotype.Service

@Service
class LikeMapper() {

    fun toLikeModel(obj: LikeDto) = LikeModel(
        id = obj.id,
        storyId = obj.storyId,
        userId = obj.userId,
    )

    fun toLikeCountModel(obj: LikeCountDto) = LikeCountModel(
        storyId = obj.storyId,
        value = obj.value,
        valueText = if (obj.value > 0) NumberUtils.toHumanReadable(obj.value) else "",
    )
}
