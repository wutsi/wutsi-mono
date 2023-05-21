package com.wutsi.event.store

interface PayloadDeserializer {
    fun deserialize(type: String, payload: String): Any?
}
