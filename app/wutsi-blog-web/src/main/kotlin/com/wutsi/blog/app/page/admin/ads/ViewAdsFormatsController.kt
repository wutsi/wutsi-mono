package com.wutsi.blog.app.page.admin.ads

import com.wutsi.blog.ads.dto.AdsType
import com.wutsi.blog.app.page.AbstractPageController
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.util.PageName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
class ViewAdsFormatsController(
    requestContext: RequestContext,
) : AbstractPageController(requestContext) {
    override fun pageName() = PageName.ADS_FORMATS_VIEW

    @GetMapping("/ads/formats/{type}")
    fun index(@PathVariable type: String, model: Model): String {
        model.addAttribute("type", AdsType.valueOf(type.uppercase()))
        return "admin/ads/format_view"
    }
}
