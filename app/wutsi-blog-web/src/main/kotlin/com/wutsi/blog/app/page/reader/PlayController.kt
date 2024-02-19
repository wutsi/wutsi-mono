package com.wutsi.blog.app.page.reader

import com.wutsi.blog.app.backend.TrackingBackend
import com.wutsi.blog.app.form.EBookRelocateForm
import com.wutsi.blog.app.form.TrackForm
import com.wutsi.blog.app.page.AbstractPageController
import com.wutsi.blog.app.service.BookService
import com.wutsi.blog.app.service.ProductService
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.util.PageName
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.tracing.TracingContext
import com.wutsi.tracking.manager.dto.PushTrackRequest
import jakarta.servlet.http.HttpServletResponse
import org.apache.commons.io.IOUtils
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import java.net.URL

@Controller
class PlayController(
    private val bookService: BookService,
    private val productService: ProductService,
    private val logger: KVLogger,
    private val trackingBackend: TrackingBackend,
    private val tracingContext: TracingContext,
    requestContext: RequestContext,
) : AbstractPageController(requestContext) {
    override fun pageName() = PageName.PLAY

    @GetMapping("/me/play/{id}")
    fun index(
        @PathVariable id: Long,
        @RequestParam(name = "request-url", required = false) returnUrl: String? = null,
        model: Model,
    ): String {
        val book = bookService.get(id)
        requestContext.checkOwnership(book)

        model.addAttribute("book", book)
        model.addAttribute("returnUrl", (returnUrl ?: "/me/library"))
        return "reader/play"
    }

    @GetMapping("/play/{id}/content.epub")
    fun content(@PathVariable id: Long, response: HttpServletResponse) {
        val book = bookService.get(id)
        if (book.expired) {
            response.sendError(404)
        } else {
            val product = book.product

            logger.add("product_id", product.id)
            logger.add("product_file_content_type", product.fileContentType)
            logger.add("product_file_url", product.fileUrl)
            if (!product.streamable) {
                response.sendError(404, "Not streamable")
            } else {
                response.contentType = product.fileContentType
                val input = URL(product.fileUrl).openStream()
                input.use {
                    IOUtils.copy(input, response.outputStream)
                }
            }
        }
    }

    @ResponseBody
    @PostMapping("/play/{id}/relocated")
    fun relocated(@PathVariable id: Long, @RequestBody request: EBookRelocateForm): Map<String, String> {
        logger.add("location", request.location)
        bookService.changeLocation(id, request)
        return emptyMap()
    }

    @ResponseBody
    @PostMapping("/play/{id}/track")
    fun track(@PathVariable id: Long, @RequestBody form: TrackForm): Map<String, String> {
        val user = requestContext.currentUser()
        if (user == null || user.superUser == true || bookService.get(id).userId == user.id) {
            logger.add("track_ignored", true)
            return emptyMap()
        }

        // Track
        trackingBackend.push(
            PushTrackRequest(
                time = form.time,
                correlationId = form.hitId,
                productId = id.toString(),
                event = form.event,
                deviceId = tracingContext.deviceId(),
                url = form.url,
                ua = form.ua,
                value = form.value,
                page = pageName(),
                referrer = form.referrer,
                accountId = user.id.toString(),
                ip = requestContext.remoteIp(),
            ),
        )

        return emptyMap()
    }
}
