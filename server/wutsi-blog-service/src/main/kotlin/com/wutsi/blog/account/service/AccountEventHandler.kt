package com.wutsi.blog.account.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.blog.account.dto.CreateLoginLinkCommand
import com.wutsi.blog.account.dto.LogoutUserCommand
import com.wutsi.blog.event.EventHandler
import com.wutsi.blog.event.EventPayload
import com.wutsi.blog.event.EventType.CREATE_LOGIN_LINK_COMMAND
import com.wutsi.blog.event.EventType.LOGOUT_USER_COMMAND
import com.wutsi.blog.event.EventType.USER_LOGGED_IN_EVENT
import com.wutsi.blog.event.RootEventHandler
import com.wutsi.platform.core.stream.Event
import org.apache.commons.text.StringEscapeUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class AccountEventHandler(
    private val root: RootEventHandler,
    private val objectMapper: ObjectMapper,
    private val service: LoginService,
) : EventHandler {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(AccountEventHandler::class.java)
    }

    @PostConstruct
    fun init() {
        root.register(USER_LOGGED_IN_EVENT, this)
        root.register(LOGOUT_USER_COMMAND, this)
        root.register(CREATE_LOGIN_LINK_COMMAND, this)
    }

    override fun handle(event: Event) {
        when (event.type) {
            USER_LOGGED_IN_EVENT -> service.onLogin(
                objectMapper.readValue(
                    decode(event.payload),
                    EventPayload::class.java,
                ),
            )

            LOGOUT_USER_COMMAND -> try {
                service.logout(
                    objectMapper.readValue(
                        decode(event.payload),
                        LogoutUserCommand::class.java,
                    ),
                )
            } catch (ex: Exception) { // Ignore the exception as this is done every hour by SessionExpirerJob
                LOGGER.warn("Unable to logout", ex)
            }

            CREATE_LOGIN_LINK_COMMAND -> service.createLoginLink(
                objectMapper.readValue(
                    decode(event.payload),
                    CreateLoginLinkCommand::class.java,
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
