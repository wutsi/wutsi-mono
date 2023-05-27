package com.wutsi.blog.app.service

import com.wutsi.blog.app.backend.LikeBackend
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.like.dto.LikeStoryCommand
import com.wutsi.blog.like.dto.SearchLikeRequest
import com.wutsi.blog.like.dto.UnlikeStoryCommand
import com.wutsi.platform.core.tracing.TracingContext
import org.springframework.stereotype.Service

@Service
class LikeService(
    private val backend: LikeBackend,
    private val requestContext: RequestContext,
    private val tracingContext: TracingContext,
) {
    fun like(storyId: Long) {
        backend.execute(
            LikeStoryCommand(
                storyId = storyId,
                userId = requestContext.currentUser()?.id,
                deviceId = tracingContext.deviceId(),
            ),
        )
    }

    fun unlike(storyId: Long) {
        backend.execute(
            UnlikeStoryCommand(
                storyId = storyId,
                userId = requestContext.currentUser()?.id,
                deviceId = tracingContext.deviceId(),
            ),
        )
    }

    fun search(storyIds: List<Long>) =
        backend.search(
            SearchLikeRequest(
                storyIds = storyIds,
                deviceId = tracingContext.deviceId(),
                userId = requestContext.currentUser()?.id,
            ),
        ).likes
}
