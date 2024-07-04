package com.wutsi.blog.app.page.mail

import com.wutsi.blog.app.backend.TrackingBackend
import com.wutsi.blog.app.service.StoryService
import com.wutsi.blog.app.util.PageName
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.tracking.manager.dto.PushTrackRequest
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.core.io.InputStreamResource
import org.springframework.http.CacheControl
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import java.util.UUID

@Controller
class PixelController(
    private val trackingBackend: TrackingBackend,
    private val storyService: StoryService,
    private val request: HttpServletRequest,
    private val logger: KVLogger,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(PixelController::class.java)
        const val REFERER = "pixel.mail.wutsi.com"
    }

    @GetMapping("/pixel/s{storyId}-u{userId}.png")
    @ResponseBody
    fun pixel(@PathVariable storyId: String, @PathVariable userId: String): ResponseEntity<InputStreamResource> {
        logger.add("story_id", storyId)
        logger.add("user_id", userId)
        logger.add("referer", request.getHeader(HttpHeaders.REFERER))

        /* Push event */
        try {
            trackingBackend.push(
                PushTrackRequest(
                    time = System.currentTimeMillis(),
                    correlationId = UUID.randomUUID().toString(),
                    page = PageName.READ,
                    productId = storyId,
                    referrer = REFERER,
                    event = "readstart",
                    ua = request.getHeader(HttpHeaders.USER_AGENT),
                    accountId = userId,
                ),
            )
        } catch (ex: Exception) {
            LOGGER.warn("Unexpected error", ex)
        }

        /* Return the pixel */
        return outputPixel()
    }

    @GetMapping("/ads/{id}/pixel/u{userId}.png")
    @ResponseBody
    fun adsPixel(
        @PathVariable id: String,
        @PathVariable userId: String,
        @RequestParam(name = "s", required = false) storyId: String? = null,
        @RequestParam(name = "b", required = false) blogId: String? = null,
    ): ResponseEntity<InputStreamResource> {
        logger.add("id", id)
        logger.add("user_id", userId)
        logger.add("referer", request.getHeader(HttpHeaders.REFERER))

        /* Push event */
        try {
            trackingBackend.push(
                PushTrackRequest(
                    time = System.currentTimeMillis(),
                    correlationId = UUID.randomUUID().toString(),
                    referrer = REFERER,
                    event = "impression",
                    ua = request.getHeader(HttpHeaders.USER_AGENT),
                    accountId = userId,
                    campaign = id,
                    productId = storyId,
                    businessId = blogId,
                ),
            )
        } catch (ex: Exception) {
            LOGGER.warn("Unexpected error", ex)
        }

        /* Return the pixel */
        return outputPixel()
    }

    private fun outputPixel(): ResponseEntity<InputStreamResource> {
        val pixel = javaClass.getResourceAsStream("/pixel/img.png")
        return ResponseEntity.ok()
            .contentType(MediaType.IMAGE_PNG)
            .cacheControl(CacheControl.noCache())
            .body(InputStreamResource(pixel))
    }
}
