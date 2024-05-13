package com.wutsi.blog.app.page.admin.ads

import com.wutsi.blog.ads.dto.AdsType
import com.wutsi.blog.app.page.AbstractPageController
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.util.PageName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/ads/formats")
class AdsFormatsController(
    requestContext: RequestContext,
) : AbstractPageController(requestContext) {
    override fun pageName() = PageName.ADS_FORMATS

    @GetMapping
    fun index(model: Model): String {
        model.addAttribute("types", AdsType.entries.filter { type -> type != AdsType.UNKNOWN })
        return "admin/ads/formats"
    }
}
