package com.wutsi.blog.mail.mapper

import com.wutsi.blog.ads.domain.AdsEntity
import com.wutsi.blog.mail.service.model.AdsModel
import com.wutsi.blog.story.domain.StoryEntity
import com.wutsi.blog.user.domain.UserEntity
import com.wutsi.platform.core.image.ImageService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.net.URLEncoder
import java.util.UUID

@Service
class AdsMapper(
    private val imageService: ImageService,
    @Value("\${wutsi.application.website-url}") private val webappUrl: String,
) {
    fun toAdsModel(ads: AdsEntity) = AdsModel(
        id = ads.id ?: "-",
        url = ads.url,
        title = ads.title,
        imageUrl = ads.imageUrl?.let { url -> imageService.transform(url = url) },
        type = ads.type,
        ctaType = ads.ctaType,
        ctaUrl = ads.url?.let {
            "$webappUrl/wclick?ads-id=${ads.id}&url=" + URLEncoder.encode(ads.url, "utf-8")
        }
    )

    fun getAdsPixelUrl(ads: AdsModel, recipient: UserEntity, story: StoryEntity? = null): String {
        val url = "$webappUrl/ads/${ads.id}/pixel/u${recipient.id}.png?rr=" + UUID.randomUUID()
        return story?.let {
            "$url&s=${story.id}&b=${story.userId}"
        } ?: url
    }
}
