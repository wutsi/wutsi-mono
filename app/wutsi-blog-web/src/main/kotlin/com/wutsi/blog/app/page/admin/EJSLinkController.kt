package com.wutsi.blog.app.page.editor

import com.wutsi.blog.app.page.editor.model.EJSLinkResponse
import com.wutsi.blog.app.service.LinkExtractorProvider
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody

@Controller
@RequestMapping("/ejs/link")
class EJSLinkController(
    private val provider: LinkExtractorProvider,
) {
    @ResponseBody
    @GetMapping(value = ["/fetch"], produces = ["application/json"])
    fun fetch(@RequestParam url: String): EJSLinkResponse {
        val meta = provider.get(url).extract(url)
        return EJSLinkResponse(
            success = 1,
            meta = meta,
        )
    }
}
