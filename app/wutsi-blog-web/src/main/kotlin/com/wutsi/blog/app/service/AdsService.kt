package com.wutsi.blog.app.service

import com.wutsi.blog.ads.dto.CreateAdsCommand
import com.wutsi.blog.ads.dto.PublishAdsCommand
import com.wutsi.blog.ads.dto.SearchAdsRequest
import com.wutsi.blog.ads.dto.UpdateAdsAttributeCommand
import com.wutsi.blog.app.backend.AdsBackend
import com.wutsi.blog.app.form.CreateAdsForm
import com.wutsi.blog.app.mapper.AdsMapper
import com.wutsi.blog.app.model.AdsModel
import org.springframework.stereotype.Component

@Component
class AdsService(
    private val backend: AdsBackend,
    private val mapper: AdsMapper,
    private val requestCcontext: RequestContext,
) {
    fun search(request: SearchAdsRequest): List<AdsModel> =
        backend.search(request).ads.map { ad -> mapper.toAdsModel(ad) }

    fun get(id: String): AdsModel =
        mapper.toAdsModel(
            backend.get(id).ads
        )

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
