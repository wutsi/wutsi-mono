package com.wutsi.blog.app.page.mail

import com.wutsi.blog.app.backend.TrackingBackend
import com.wutsi.blog.app.util.PageName
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.tracking.manager.dto.PushTrackRequest
import org.apache.commons.io.IOUtils
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.ResponseBody
import java.util.UUID
import javax.servlet.http.HttpServletRequest

@Controller
class PixelController(
    private val trackingBackend: TrackingBackend,
    private val request: HttpServletRequest,
    private val logger: KVLogger,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(PixelController::class.java)
    }

    @GetMapping("/pixel/s{storyId}-u{userId}.png", produces = [MediaType.IMAGE_PNG_VALUE])
    @ResponseBody
    fun pixel(@PathVariable storyId: String, @PathVariable userId: String): ByteArray? {
        logger.add("story_id", storyId)
        logger.add("user_id", userId)
        logger.add("referer", request.getHeader(HttpHeaders.REFERER))
        try {
            trackingBackend.push(
                PushTrackRequest(
                    time = System.currentTimeMillis(),
                    correlationId = UUID.randomUUID().toString(),
                    page = PageName.READ,
                    productId = storyId,
                    referrer = request.getHeader(HttpHeaders.REFERER),
                    event = "readstart",
                    ua = request.getHeader(HttpHeaders.USER_AGENT),
                    accountId = userId,
                ),
            )
        } catch (ex: Exception) {
            LOGGER.warn("Unexpected error", ex)
        } finally {
            val pixel = javaClass.getResourceAsStream("/pixel/img.png")
            return IOUtils.toByteArray(pixel)
        }
    }
}
