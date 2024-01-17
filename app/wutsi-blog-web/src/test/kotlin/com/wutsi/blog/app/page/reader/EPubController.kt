package com.wutsi.blog.app.page.reader

import jakarta.servlet.http.HttpServletResponse
import org.apache.commons.io.IOUtils
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class EPubController {
    @GetMapping("/document.epub")
    fun document(response: HttpServletResponse) {
        val input = EPubController::class.java.getResource("/public/assets/document.epub")
        response.contentType = "application/epub+gzip"
        IOUtils.copy(input, response.outputStream)
    }
}
