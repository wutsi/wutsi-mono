package com.wutsi.blog.mail.endpoint

import com.wutsi.blog.event.EventType
import com.wutsi.blog.event.StreamId
import com.wutsi.blog.mail.dao.XEmailRepository
import com.wutsi.blog.mail.dto.NotificationType
import com.wutsi.blog.mail.service.ses.SESBounce
import com.wutsi.blog.mail.service.ses.SESComplaint
import com.wutsi.blog.mail.service.ses.SESNotification
import com.wutsi.blog.mail.service.ses.SESRecipient
import com.wutsi.event.store.EventStore
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
@Sql(value = ["/db/clean.sql", "/db/mail/SESWebhook.sql"])
class SESWebhookTest {
    @LocalServerPort
    private lateinit var port: Integer

    @Autowired
    private lateinit var rest: TestRestTemplate

    @Autowired
    private lateinit var dao: XEmailRepository

    @Autowired
    private lateinit var eventStore: EventStore

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

        val bounce1 = dao.findByEmail("ray.sponsible@gmail.com").get()
        assertEquals(NotificationType.BOUNCE, bounce1.type)

        val bounce2 = dao.findByEmail("roger.milla@gmail.com").get()
        assertEquals(NotificationType.BOUNCE, bounce2.type)

        val events = eventStore.events(
            streamId = StreamId.EMAIL_NOTIFICATION,
            type = EventType.EMAIL_BOUNCED_EVENT,
        ).sortedBy { it.timestamp }
        assertEquals(2, events.size)
        assertEquals("Ray.sponsible@gmail.com", events[0].metadata?.get("email"))
        assertEquals("roger.milla@gmail.com", events[1].metadata?.get("email"))
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

        val bounce = dao.findByEmail("user.01@gmail.com").get()
        assertEquals(NotificationType.COMPLAIN, bounce.type)

        val events = eventStore.events(
            streamId = StreamId.EMAIL_NOTIFICATION,
            type = EventType.EMAIL_COMPLAINED_EVENT,
        ).sortedBy { it.timestamp }
        assertEquals(1, events.size)
        assertEquals("user.01@gmail.com", events[0].metadata?.get("email"))
    }

    @Test
    fun alreadyBlacklisted() {
        // WHEN
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
        val response = rest.postForEntity("/webhooks/ses", request, Any::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val bounce = dao.findByEmail("bounced1@hotmail.com").get()
        assertTrue(bounce.creationDateTime.before(Date()))
    }

    @Test
    fun confirmation() {
        val url = URL("http://localhost:$port/webhooks/ses")
        val httpURLConnection = url.openConnection() as HttpURLConnection
        httpURLConnection.requestMethod = "POST"
        httpURLConnection.setRequestProperty("Content-Type", "text/plain;charset=UTF-8")
        httpURLConnection.setRequestProperty("Accept", "text/plain")
        httpURLConnection.setRequestProperty("Content-Encoding", "UTF-8")
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

    @Test
    fun bounceText() {
        val url = URL("http://localhost:$port/webhooks/ses")
        val httpURLConnection = url.openConnection() as HttpURLConnection
        httpURLConnection.requestMethod = "POST"
        httpURLConnection.setRequestProperty("Content-Type", "text/plain;charset=UTF-8")
        httpURLConnection.setRequestProperty("Accept", "text/plain")
        httpURLConnection.setRequestProperty("Content-Encoding", "UTF-8")
        httpURLConnection.doInput = true
        httpURLConnection.doOutput = true

        // Send the JSON we created
        val outputStreamWriter = OutputStreamWriter(httpURLConnection.outputStream)
        outputStreamWriter.write(
            """
                {
                   "notificationType":"Bounce",
                   "bounce":{
                      "bounceType":"Permanent",
                      "reportingMTA":"dns; email.example.com",
                      "bouncedRecipients":[
                         {
                            "emailAddress":"jane@example.com",
                            "status":"5.1.1",
                            "action":"failed",
                            "diagnosticCode":"smtp; 550 5.1.1 <jane@example.com>... User"
                         }
                      ],
                      "bounceSubType":"General",
                      "timestamp":"2016-01-27T14:59:38.237Z",
                      "feedbackId":"00000138111222aa-33322211-cccc-cccc-cccc-ddddaaaa068a-000000",
                      "remoteMtaIp":"127.0.2.0"
                   },
                   "mail":{
                      "timestamp":"2016-01-27T14:59:38.237Z",
                      "source":"john@example.com",
                      "sourceArn": "arn:aws:ses:us-east-1:888888888888:identity/example.com",
                      "sourceIp": "127.0.3.0",
                      "sendingAccountId":"123456789012",
                      "callerIdentity": "IAM_user_or_role_name",
                      "messageId":"00000138111222aa-33322211-cccc-cccc-cccc-ddddaaaa0680-000000",
                      "destination":[
                        "jane@example.com",
                        "mary@example.com",
                        "richard@example.com"],
                      "headersTruncated":false,
                      "headers":[
                       {
                         "name":"From",
                         "value":"\"John Doe\" <john@example.com>"
                       },
                       {
                         "name":"To",
                         "value":"\"Jane Doe\" <jane@example.com>, \"Mary Doe\" <mary@example.com>, \"Richard Doe\" <richard@example.com>"
                       },
                       {
                         "name":"Message-ID",
                         "value":"custom-message-ID"
                       },
                       {
                         "name":"Subject",
                         "value":"Hello"
                       },
                       {
                         "name":"Content-Type",
                         "value":"text/plain; charset=\"UTF-8\""
                       },
                       {
                         "name":"Content-Transfer-Encoding",
                         "value":"base64"
                       },
                       {
                         "name":"Date",
                         "value":"Wed, 27 Jan 2016 14:05:45 +0000"
                       }
                      ],
                      "commonHeaders":{
                         "from":[
                            "John Doe <john@example.com>"
                         ],
                         "date":"Wed, 27 Jan 2016 14:05:45 +0000",
                         "to":[
                            "Jane Doe <jane@example.com>, Mary Doe <mary@example.com>, Richard Doe <richard@example.com>"
                         ],
                         "messageId":"custom-message-ID",
                         "subject":"Hello"
                       }
                    }
                }
            """.trimIndent()
        )
        outputStreamWriter.flush()

        assertEquals(HttpURLConnection.HTTP_OK, httpURLConnection.responseCode)

        Thread.sleep(15000)
        val bounce = dao.findByEmail("jane@example.com").get()
        assertTrue(bounce.creationDateTime.before(Date()))
    }
}
