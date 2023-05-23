package com.wutsi.blog.like

import com.wutsi.blog.client.event.LikeEvent
import com.wutsi.blog.client.like.CountLikeResponse
import com.wutsi.blog.client.like.CreateLikeRequest
import com.wutsi.blog.client.like.CreateLikeResponse
import com.wutsi.blog.client.like.GetLikeResponse
import com.wutsi.blog.client.like.SearchLikeRequest
import com.wutsi.blog.client.like.SearchLikeResponse
import com.wutsi.blog.like.mapper.LikeMapper
import com.wutsi.blog.like.service.LikeServiceV0
import com.wutsi.platform.core.tracing.TracingContext
import org.springframework.context.ApplicationEventPublisher
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.Date
import javax.validation.Valid

@Deprecated("")
@RestController
@RequestMapping("/v1/likes")
class LikeController(
    private val service: LikeServiceV0,
    private val mapper: LikeMapper,
    private val events: ApplicationEventPublisher,
) {
    @GetMapping("/{id}")
    fun get(@PathVariable id: Long): GetLikeResponse =
        GetLikeResponse(
            like = mapper.toLikeDto(
                service.findLike(id),
            ),
        )

    @PostMapping
    fun create(
        @Valid @RequestBody request: CreateLikeRequest,
        @RequestHeader(TracingContext.HEADER_DEVICE_ID, required = false) deviceId: String? = null,
    ): CreateLikeResponse {
        val like = service.create(request, deviceId)
        events.publishEvent(LikeEvent(likeId = like.id!!))
        return CreateLikeResponse(
            likeId = like.id,
        )
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) = service.delete(id)

    @GetMapping
    fun search(
        @RequestParam(required = false) authorId: Long? = null,
        @RequestParam(required = false) userId: Long? = null,
        @RequestParam(required = false) storyId: List<Long>? = null,
        @RequestParam(required = false) deviceId: String? = null,
        @RequestParam(required = false) @DateTimeFormat(
            pattern = "yyyy-MM-dd",
        ) since: Date? = null,
        @RequestParam(required = false, defaultValue = "20") limit: Int = 20,
        @RequestParam(required = false, defaultValue = "0") offset: Int = 0,
    ): SearchLikeResponse {
        val likes = service.search(
            SearchLikeRequest(
                authorId = authorId,
                userId = userId,
                storyIds = storyId?.let { it } ?: emptyList(),
                deviceId = deviceId,
                since = since,
                limit = limit,
                offset = offset,
            ),
        )
        return SearchLikeResponse(
            likes = likes.map { mapper.toLikeDto(it) },
        )
    }

    @PostMapping("/search")
    fun search(request: SearchLikeRequest): SearchLikeResponse {
        val likes = service.search(request)
        return SearchLikeResponse(
            likes = likes.map { mapper.toLikeDto(it) },
        )
    }

    @GetMapping("/count")
    fun count(
        @RequestParam(required = false) authorId: Long? = null,
        @RequestParam(required = false) userId: Long? = null,
        @RequestParam(required = false) storyId: List<Long>? = null,
        @RequestParam(required = false) since: Date? = null,
    ): CountLikeResponse {
        val counts = service.count(
            SearchLikeRequest(
                authorId = authorId,
                userId = userId,
                storyIds = storyId?.let { it } ?: emptyList(),
                since = since,
            ),
        )
        return CountLikeResponse(
            counts = counts.map { mapper.toLikeCountDto(it) },
        )
    }

    @PostMapping("/count")
    fun count(request: SearchLikeRequest): CountLikeResponse {
        val counts = service.count(request)
        return CountLikeResponse(
            counts = counts.map { mapper.toLikeCountDto(it) },
        )
    }
}
