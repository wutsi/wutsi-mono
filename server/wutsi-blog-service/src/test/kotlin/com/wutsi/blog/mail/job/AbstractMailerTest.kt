package com.wutsi.blog.mail.job

import com.icegreen.greenmail.util.GreenMail
import com.icegreen.greenmail.util.ServerSetup
import jakarta.mail.Message
import jakarta.mail.internet.MimeMessage
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class AbstractMailerTest {
    @Value("\${spring.mail.port}")
    private lateinit var port: String

    protected lateinit var smtp: GreenMail

    @BeforeEach
    fun setUp() {
        smtp = GreenMail(ServerSetup.SMTP.port(port.toInt()))
        smtp.setUser("wutsi", "secret")
        smtp.start()
    }

    @AfterEach
    fun tearDown() {
        if (smtp.isRunning) {
            smtp.stop()
        }
    }

    protected fun deliveredTo(email: String, messages: Array<MimeMessage>): Boolean =
        messages.find { message ->
            message.getRecipients(Message.RecipientType.TO).find {
                it.toString().contains(email)
            } != null
        } != null

    protected fun hasHeader(name: String, value: String, message: MimeMessage): Boolean {
        val headers = message.allHeaders.toList()
        val header = headers.find {
            it.name.contains(name, true)
        }
        return header?.value == value
    }

    protected fun print(message: MimeMessage) {
        println("------------------------------")
        println(message.content.toString())
    }
}
