package com.wutsi.blog.app.page.store

import com.wutsi.blog.app.service.LiretamaService
import com.wutsi.blog.app.service.ProductService
import com.wutsi.extractor.Downloader
import com.wutsi.extractor.ImageExtractor
import com.wutsi.platform.core.logging.KVLogger
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import java.net.URL

@Controller
class LiretamaController(
    private val productService: ProductService,
    private val liretamaService: LiretamaService,
    private val logger: KVLogger,
    private val downloader: Downloader,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(LiretamaController::class.java)
    }

    @GetMapping("/liretama/{id}")
    fun index(
        @PathVariable id: Long,
        response: HttpServletResponse,
    ) {
        val product = productService.get(id)
        val redirectUrl = liretamaService.toUrl(product)

        logger.add("redirect_url", redirectUrl)
        logger.add("liretama_url", product.liretamaUrl)
        response.sendRedirect(redirectUrl)
    }

    @GetMapping("/liretama/image")
    fun image(@RequestParam url: String, model: Model): String {
        try {
            val html = downloader.download(URL(url))
            val imageUrl: String? = ImageExtractor().extract(html).ifEmpty { null }
            model.addAttribute("imageUrl", imageUrl)
        } catch (ex: Exception) {
            LOGGER.warn("Unable to load image", ex)
        }
        return "store/liretama"
    }
}
