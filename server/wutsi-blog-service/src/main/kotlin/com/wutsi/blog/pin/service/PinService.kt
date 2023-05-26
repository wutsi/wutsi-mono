package com.wutsi.blog.pin.service

import com.wutsi.blog.client.pin.CreatePinRequest
import com.wutsi.blog.pin.dao.PinRepository
import com.wutsi.blog.pin.domain.Pin
import com.wutsi.blog.story.domain.Story
import com.wutsi.blog.story.service.StoryService
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.exception.ForbiddenException
import com.wutsi.platform.core.error.exception.NotFoundException
import org.springframework.stereotype.Service
import java.util.Date

@Deprecated("")
@Service
class PinServiceV0(
    private val storyService: StoryService,
    private val dao: PinRepository,
) {
    fun get(userId: Long): Pin =
        dao.findById(userId)
            .orElseThrow { NotFoundException(Error("pin_not_found")) }

    fun create(userId: Long, request: CreatePinRequest): Pin {
        val story = storyService.findById(request.storyId!!)
        checkPermission(userId, story)

        var pin: Pin
        try {
            pin = get(userId)
            pin.storyId = story.id!!
            pin.creationDateTime = Date()
        } catch (ex: NotFoundException) {
            pin = Pin(
                userId = story.userId,
                storyId = story.id!!,
                creationDateTime = Date(),
            )
        }

        dao.save(pin)
        return pin
    }

    fun delete(id: Long) {
        val pin = dao.findById(id)
        if (pin.isPresent) {
            dao.delete(pin.get())
        }
    }

    fun checkPermission(userId: Long, story: Story) {
        if (story.userId != userId) {
            throw ForbiddenException(Error("not_owner"))
        }
    }
}
