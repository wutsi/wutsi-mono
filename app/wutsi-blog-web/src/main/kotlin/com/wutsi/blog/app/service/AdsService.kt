package com.wutsi.blog.app.service

import com.wutsi.blog.ads.dto.CreateAdsCommand
import com.wutsi.blog.ads.dto.PublishAdsCommand
import com.wutsi.blog.ads.dto.SearchAdsRequest
import com.wutsi.blog.ads.dto.UpdateAdsAttributeCommand
import com.wutsi.blog.app.backend.AdsBackend
import com.wutsi.blog.app.form.CreateAdsForm
import com.wutsi.blog.app.mapper.AdsMapper
import com.wutsi.blog.app.model.AdsModel
import com.wutsi.blog.product.dto.SearchCategoryRequest
import org.springframework.stereotype.Component

@Component
class AdsService(
    private val backend: AdsBackend,
    private val mapper: AdsMapper,
    private val categoryService: CategoryService,
    private val requestCcontext: RequestContext,
) {
    fun search(request: SearchAdsRequest): List<AdsModel> =
        backend.search(request).ads.map { ad -> mapper.toAdsModel(ad) }

    fun get(id: String): AdsModel {
        val ads = backend.get(id).ads
        val categories = ads.categoryId?.let { categoryId ->
            categoryService.search(
                SearchCategoryRequest(
                    categoryIds = listOf(categoryId),
                    limit = 1
                )
            )
        }
        return mapper.toAdsModel(ads, categories?.firstOrNull())
    }

    fun create(form: CreateAdsForm): String =
        backend.create(
            CreateAdsCommand(
                userId = requestCcontext.currentUser()?.id ?: -1,
                title = form.title,
                type = form.type,
                currency = AdsModel.DEFAULT_CURRENCY
            )
        ).adsId

    fun updateAttribute(id: String, form: UpdateAdsAttributeCommand) {
        backend.updateAttribute(
            request = UpdateAdsAttributeCommand(
                name = form.name,
                value = form.value,
                adsId = id,
            )
        )
    }

    fun publish(id: String) {
        backend.publish(PublishAdsCommand(id))
    }
}
