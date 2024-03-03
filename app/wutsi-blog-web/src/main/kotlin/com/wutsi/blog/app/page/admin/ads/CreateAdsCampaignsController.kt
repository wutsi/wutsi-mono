package com.wutsi.blog.app.page.admin.ads

import com.wutsi.blog.app.form.CreateAdsForm
import com.wutsi.blog.app.form.CreateProductForm
import com.wutsi.blog.app.page.AbstractStoreController
import com.wutsi.blog.app.service.AdsService
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.util.PageName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/me/ads/campaigns/create")
class CreateAdsCampaignsController(
    private val service: AdsService,
    requestContext: RequestContext,
) : AbstractStoreController(requestContext) {
    override fun pageName() = PageName.ADS_CAMPAIGNS_CREATE

    @GetMapping
    fun index(model: Model): String {
        model.addAttribute("form", CreateProductForm())

        return "admin/ads/create"
    }

    @PostMapping
    fun submit(@ModelAttribute form: CreateAdsForm): String {
        val adsId = service.create(form)
        return "redirect:/me/ads/campaigns/$adsId"
    }
}
