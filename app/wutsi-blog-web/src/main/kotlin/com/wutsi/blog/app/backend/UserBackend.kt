package com.wutsi.blog.app.backend

import com.wutsi.blog.user.dto.UpdateUserAttributeCommand
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class UserBackend(private val rest: RestTemplate) {
    @Value("\${wutsi.application.backend.user.endpoint}")
    private lateinit var endpoint: String

    fun execute(command: UpdateUserAttributeCommand) {
        rest.postForEntity("$endpoint/commands/update-attribute", command, Any::class.java)
    }
}
