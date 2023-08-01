package com.wutsi.blog.app.page.reader

import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.event.EventType.STORY_ATTACHMENT_DOWNLOADED_EVENT
import com.wutsi.blog.story.dto.StoryAttachmentDownloadedEventPayload
import com.wutsi.platform.core.storage.StorageService
import com.wutsi.platform.core.stream.EventStream
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import java.net.URL
import java.net.URLConnection
import java.util.Base64
import javax.servlet.http.HttpServletResponse

@Controller
class AttachmentController(
    private val storage: StorageService,
    private val requestContext: RequestContext,
    private val eventStream: EventStream,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(AttachmentController::class.java)
    }

    @GetMapping("/attachment/download")
    fun download(
        @RequestParam(name = "l") link: String,
        @RequestParam(name = "f") filename: String,
        @RequestParam(name = "s", required = false) storyId: Long? = null,
        response: HttpServletResponse,
    ) {
        // Download
        val url = String(Base64.getDecoder().decode(link))
        val contentType = URLConnection.guessContentTypeFromName(filename) ?: "application/octet-stream"
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"${filename}\"")
        response.setHeader(HttpHeaders.CONTENT_TYPE, contentType)
        storage.get(URL(url), response.outputStream)

        // Notification
        try {
            storyId?.let {
                eventStream.publish(
                    type = STORY_ATTACHMENT_DOWNLOADED_EVENT,
                    payload = StoryAttachmentDownloadedEventPayload(
                        userId = requestContext.currentUser()?.id,
                        storyId = storyId,
                        filename = filename,
                        subscribe = true,
                    ),
                )
            }
        } catch (ex: Exception) {
            LOGGER.warn("Unable to push notification", ex)
        }
    }
}
