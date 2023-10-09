package com.wutsi.blog.endorsement.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.blog.endorsement.dto.EndorseUserCommand
import com.wutsi.blog.event.EventHandler
import com.wutsi.blog.event.EventPayload
import com.wutsi.blog.event.EventType.ENDORSE_USER_COMMAND
import com.wutsi.blog.event.EventType.USER_ENDORSED_EVENT
import com.wutsi.blog.event.RootEventHandler
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.stream.Event
import org.apache.commons.text.StringEscapeUtils
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class EndorsementEventHandler(
    private val root: RootEventHandler,
    private val objectMapper: ObjectMapper,
    private val service: EndorsementService,
    private val logger: KVLogger,
) : EventHandler {
    @PostConstruct
    fun init() {
        root.register(ENDORSE_USER_COMMAND, this)

        root.register(USER_ENDORSED_EVENT, this)
    }

    override fun handle(event: Event) {
        when (event.type) {
            USER_ENDORSED_EVENT -> service.onEndorsed(
                objectMapper.readValue(
                    decode(event.payload),
                    EventPayload::class.java,
                ),
            )

            ENDORSE_USER_COMMAND -> try {
                service.endorse(
                    objectMapper.readValue(
                        decode(event.payload),
                        EndorseUserCommand::class.java,
                    ),
                )
            } catch (e: DataIntegrityViolationException) {
                // Ignore - duplicate like
                logger.add("duplicate_endorsement", true)
            }

            else -> {}
        }
    }

    private fun decode(json: String): String =
        StringEscapeUtils.unescapeJson(json)
            .replace("\"{", "{")
            .replace("}\"", "}")
}
