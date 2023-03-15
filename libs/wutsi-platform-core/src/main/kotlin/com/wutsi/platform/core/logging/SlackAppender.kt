package com.wutsi.platform.core.logging

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.Layout
import ch.qos.logback.core.LayoutBase
import ch.qos.logback.core.UnsynchronizedAppenderBase
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.logging.log4j.Level
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL

class SlackAppender(var layout: Layout<ILoggingEvent> = DEFAULT_LAYOUT) : UnsynchronizedAppenderBase<ILoggingEvent>() {
    companion object {
        private val DEFAULT_LAYOUT: Layout<ILoggingEvent> = object : LayoutBase<ILoggingEvent>() {
            override fun doLayout(event: ILoggingEvent): String {
                return event.formattedMessage.replace("\n".toRegex(), "\n\t")
            }
        }
    }

    private val timeout = 30000
    private val objectMapper = ObjectMapper()
    private var webhookUrl: String? = ""

    init {
        webhookUrl = System.getenv()["LOG4J_SLACK_WEBHOOK_URL"]
        println("LOG4J_SLACK_WEBHOOK_URL=$webhookUrl")
    }

    override fun append(event: ILoggingEvent) {
        if (webhookUrl.isNullOrEmpty()) {
            return
        }

        try {
            val parts = layout.doLayout(event).split("\n".toRegex(), 2)
            val message: MutableMap<String, Any> = HashMap()
            message["text"] = parts[0]

            // Send the lines below the first line as an attachment.
            if (parts.size > 1 && parts[1].isNotEmpty()) {
                val attachment: MutableMap<String, String> = HashMap()
                attachment["text"] = parts[1]
                attachment["color"] = toColor(event)
                message["attachments"] = listOf<Map<String, String>>(attachment)
            }
            postMessage(webhookUrl!!, message)
        } catch (ex: Exception) {
            ex.printStackTrace(System.err)
        }
    }

    private fun postMessage(url: String, message: Map<String, Any>) {
        val bytes = objectMapper.writeValueAsBytes(message)
        val cnn: HttpURLConnection = URL(url).openConnection() as HttpURLConnection
        try {
            cnn.connectTimeout = timeout
            cnn.readTimeout = timeout
            cnn.doOutput = true
            cnn.requestMethod = "POST"
            cnn.setFixedLengthStreamingMode(bytes.size)
            cnn.setRequestProperty("Content-Type", "application/json")
            val os: OutputStream = cnn.outputStream
            try {
                os.write(bytes)
                os.flush()
            } finally {
                os.close()
            }
        } finally {
            cnn.disconnect()
        }
    }

    private fun toColor(evt: ILoggingEvent): String {
        if (Level.WARN.equals(evt.level)) {
            return "warning"
        } else if (Level.INFO.equals(evt.level)) {
            return "good"
        }
        return "error"
    }
}
