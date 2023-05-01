package com.wutsi.blog.sdk

import com.wutsi.blog.client.pin.CreatePinRequest
import com.wutsi.blog.client.pin.CreatePinResponse
import com.wutsi.blog.client.pin.GetPinResponse

interface PinApi {
    fun get(userId: Long): GetPinResponse
    fun create(userId: Long, request: CreatePinRequest): CreatePinResponse
    fun delete(userId: Long)
}
