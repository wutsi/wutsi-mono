package com.wutsi.blog.app.service

import com.wutsi.blog.app.backend.PinBackend
import com.wutsi.blog.pin.dto.PinStory
import com.wutsi.blog.pin.dto.PinStoryCommand
import com.wutsi.blog.pin.dto.SearchPinRequest
import com.wutsi.blog.pin.dto.UnpinStoryCommand
import org.springframework.stereotype.Service

@Service
class PinService(
    private val backend: PinBackend,
) {
    fun pin(storyId: Long) {
        backend.execute(
            PinStoryCommand(
                storyId = storyId,
            ),
        )
    }

    fun unpin(storyId: Long) {
        backend.execute(
            UnpinStoryCommand(
                storyId = storyId,
            ),
        )
    }

    fun get(userId: Long): PinStory? {
        val pins = search(listOf(userId))
        return if (pins.size == 1) {
            pins[0]
        } else {
            null
        }
    }

    fun search(userIds: List<Long>) =
        backend.search(
            SearchPinRequest(
                userIds = userIds,
            ),
        ).pins
}
