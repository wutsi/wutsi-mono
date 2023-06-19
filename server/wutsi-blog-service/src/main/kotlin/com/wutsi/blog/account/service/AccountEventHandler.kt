package com.wutsi.blog.account.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.blog.event.EventHandler
import com.wutsi.blog.event.EventPayload
import com.wutsi.blog.event.EventType.USER_LOGGED_IN_EVENT
import com.wutsi.blog.event.RootEventHandler
import com.wutsi.platform.core.stream.Event
import org.apache.commons.text.StringEscapeUtils
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class AccountEventHandler(
    private val root: RootEventHandler,
    private val objectMapper: ObjectMapper,
    private val service: LoginService,
) : EventHandler {
    @PostConstruct
    fun init() {
        root.register(USER_LOGGED_IN_EVENT, this)
    }

    override fun handle(event: Event) {
        when (event.type) {
            USER_LOGGED_IN_EVENT -> service.onLogin(
                objectMapper.readValue(
                    decode(event.payload),
                    EventPayload::class.java,
                ),
            )

            else -> {}
        }
    }

    private fun decode(json: String): String =
        StringEscapeUtils.unescapeJson(json)
            .replace("\"{", "{")
            .replace("}\"", "}")
}
