package com.wutsi.blog.mail.endpoint

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.blog.mail.service.XEmailService
import com.wutsi.blog.mail.service.ses.SESNotification
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/webhooks/ses")
class SESWebhook(
    private val objectMapper: ObjectMapper,
    private val xmailService: XEmailService,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(SESWebhook::class.java)
    }

    @PostMapping(consumes = ["text/plain;charset=UTF-8"], produces = [MediaType.TEXT_PLAIN_VALUE])
    fun notify(request: HttpServletRequest) {
        val req = objectMapper.readValue(request.inputStream, SESNotification::class.java)
        if (req.type == "SubscriptionConfirmation") {
            LOGGER.info(">>> Confirmation Message{\n" + objectMapper.writeValueAsString(req))
        } else {
            notify(req)
        }
    }

    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun notify(@RequestBody request: SESNotification) {
        xmailService.process(request)
    }
}
