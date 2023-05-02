package com.wutsi.blog.channel

import com.wutsi.blog.channel.mapper.ChannelMapper
import com.wutsi.blog.channel.service.ChannelService
import com.wutsi.blog.client.channel.ChannelType
import com.wutsi.blog.client.channel.CreateChannelRequest
import com.wutsi.blog.client.channel.CreateChannelResponse
import com.wutsi.blog.client.channel.GetChannelResponse
import com.wutsi.blog.client.channel.SearchChannelResponse
import com.wutsi.blog.client.event.ChannelBoundedEvent
import com.wutsi.blog.client.event.ChannelUnboundedEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/v1/channels")
class ChannelController(
    private val service: ChannelService,
    private val mapper: ChannelMapper,
    private val eventPublisher: ApplicationEventPublisher,
) {
    @GetMapping()
    fun search(@RequestParam userId: Long): SearchChannelResponse {
        val channels = service.search(userId)
        return SearchChannelResponse(channels = channels.map { mapper.toChannelDto(it) })
    }

    @PostMapping()
    fun create(@RequestBody @Valid request: CreateChannelRequest): CreateChannelResponse {
        val response = CreateChannelResponse(
            channelId = service.create(request).id!!,
        )
        eventPublisher.publishEvent(ChannelBoundedEvent(response.channelId))
        return response
    }

    @GetMapping("/{id}")
    fun get(@PathVariable id: Long): GetChannelResponse =
        GetChannelResponse(
            channel = mapper.toChannelDto(service.findById(id)),
        )

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) {
        val channel = service.delete(id)
        eventPublisher.publishEvent(
            ChannelUnboundedEvent(
                channelId = id,
                userId = channel.userId,
                channelType = channel.type,
            ),
        )
    }

    @GetMapping("/sync")
    fun sync(@RequestParam type: ChannelType) {
        val channels = service.findByType(type)
        channels.forEach {
            eventPublisher.publishEvent(ChannelBoundedEvent(it.id!!))
        }
    }
}
