package com.wutsi.blog.pin

import com.wutsi.blog.client.pin.CreatePinRequest
import com.wutsi.blog.client.pin.CreatePinResponse
import com.wutsi.blog.client.pin.GetPinResponse
import com.wutsi.blog.pin.mapper.PinMapper
import com.wutsi.blog.pin.service.PinService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
class PinController(
    private val service: PinService,
    private val mapper: PinMapper,
) {
    @GetMapping("/v1/users/{id}/pin")
    fun get(@PathVariable id: Long): GetPinResponse =
        GetPinResponse(pin = mapper.toPinDto(service.get(id)))

    @PostMapping("/v1/users/{id}/pin")
    fun create(@PathVariable id: Long, @Valid @RequestBody request: CreatePinRequest): CreatePinResponse =
        CreatePinResponse(pinId = service.create(id, request).userId)

    @DeleteMapping("/v1/users/{id}/pin")
    fun delete(@PathVariable id: Long) =
        service.delete(id)
}
