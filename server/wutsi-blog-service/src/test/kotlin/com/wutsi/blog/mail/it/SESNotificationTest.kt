package com.wutsi.blog.mail.it

import com.wutsi.blog.mail.dao.XEmailRepository
import com.wutsi.blog.mail.dto.NotificationType
import com.wutsi.blog.mail.service.ses.SESBounce
import com.wutsi.blog.mail.service.ses.SESComplaint
import com.wutsi.blog.mail.service.ses.SESNotification
import com.wutsi.blog.mail.service.ses.SESRecipient
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.jdbc.Sql
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.util.Date
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Sql(value = ["/db/clean.sql", "/db/mail/SESNotification.sql"])
class SESNotificationTest {
    @LocalServerPort
    private lateinit var port: Integer

    @Autowired
    private lateinit var rest: TestRestTemplate

    @Autowired
    private lateinit var dao: XEmailRepository

    @Test
    fun permanentBounce() {
        // GIVEN
        val request = SESNotification(
            notificationType = "Bounce",
            bounce = SESBounce(
                bounceType = "Permanent",
                bouncedRecipients = listOf(
                    SESRecipient(
                        emailAddress = "Ray.sponsible@gmail.com",
                    ),
                    SESRecipient(
                        emailAddress = "roger.milla@gmail.com",
                    ),
                )
            )
        )

        // WHEN
        val response = rest.postForEntity("/webhooks/ses", request, Any::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        Thread.sleep(15000)

        val bounce1 = dao.findByEmail("ray.sponsible@gmail.com").get()
        assertEquals(NotificationType.BOUNCE, bounce1.type)

        val bounce2 = dao.findByEmail("roger.milla@gmail.com").get()
        assertEquals(NotificationType.BOUNCE, bounce2.type)
    }

    @Test
    fun softBounce() {
        // GIVEN
        val request = SESNotification(
            notificationType = "Bounce",
            bounce = SESBounce(
                bounceType = "Soft",
                bouncedRecipients = listOf(
                    SESRecipient(
                        emailAddress = "joe.smith@gmail.com",
                    ),
                )
            )
        )

        // WHEN
        val response = rest.postForEntity("/webhooks/ses", request, Any::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        Thread.sleep(15000)

        val bounce = dao.findByEmail("joe.smith@gmail.com")
        assertFalse(bounce.isPresent)
    }

    @Test
    fun complaint() {
        // GIVEN
        val request = SESNotification(
            notificationType = "Complaint",
            complaint = SESComplaint(
                complainedRecipients = listOf(
                    SESRecipient(
                        emailAddress = "user.01@gmail.com",
                    ),
                )
            )
        )

        // WHEN
        val response = rest.postForEntity("/webhooks/ses", request, Any::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        Thread.sleep(15000)

        val bounce = dao.findByEmail("user.01@gmail.com").get()
        assertEquals(NotificationType.COMPLAIN, bounce.type)
    }

    @Test
    fun alreadyBlacklisted() {
        // GIVEN
        val request = SESNotification(
            notificationType = "Complaint",
            complaint = SESComplaint(
                complainedRecipients = listOf(
                    SESRecipient(
                        emailAddress = "bounced1@hotmail.com",
                    ),
                )
            )
        )
        Thread.sleep(1000)

        // WHEN
        val response = rest.postForEntity("/webhooks/ses", request, Any::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        Thread.sleep(15000)

        val bounce = dao.findByEmail("bounced1@hotmail.com").get()
        assertTrue(bounce.creationDateTime.before(Date()))
    }

    @Test
    fun confirmation() {
        val url = URL("http://localhost:$port/webhooks/ses")
        val httpURLConnection = url.openConnection() as HttpURLConnection
        httpURLConnection.requestMethod = "POST"
        httpURLConnection.setRequestProperty("Content-Type", "text/plain")
        httpURLConnection.setRequestProperty("Accept", "text/plain")
        httpURLConnection.doInput = true
        httpURLConnection.doOutput = true

        // Send the JSON we created
        val outputStreamWriter = OutputStreamWriter(httpURLConnection.outputStream)
        outputStreamWriter.write(
            """
                {
                  "Type" : "SubscriptionConfirmation",
                  "MessageId" : "165545c9-2a5c-472c-8df2-7ff2be2b3b1b",
                  "Token" : "2336412f37f...",
                  "TopicArn" : "arn:aws:sns:us-west-2:123456789012:MyTopic",
                  "Message" : "You have chosen to subscribe to the topic arn:aws:sns:us-west-2:123456789012:MyTopic.\nTo confirm the subscription, visit the SubscribeURL included in this message.",
                  "SubscribeURL" : "https://sns.us-west-2.amazonaws.com/?Action=ConfirmSubscription&TopicArn=arn:aws:sns:us-west-2:123456789012:MyTopic&Token=2336412f37...",
                  "Timestamp" : "2012-04-26T20:45:04.751Z",
                  "SignatureVersion" : "1",
                  "Signature" : "EXAMPLEpH+...",
                  "SigningCertURL" : "https://sns.us-west-2.amazonaws.com/SimpleNotificationService-f3ecfb7224c7233fe7bb5f59f96de52f.pem"
                }
            """.trimIndent()
        )
        outputStreamWriter.flush()

        assertEquals(HttpURLConnection.HTTP_OK, httpURLConnection.responseCode)
    }
}
