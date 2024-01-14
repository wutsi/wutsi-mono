package com.wutsi.blog.mail.mapper

import com.wutsi.blog.country.dto.Country
import com.wutsi.blog.mail.service.MailContext
import com.wutsi.blog.mail.service.model.LinkModel
import com.wutsi.blog.product.domain.ProductEntity
import com.wutsi.blog.product.domain.StoreEntity
import com.wutsi.blog.product.dto.Offer
import com.wutsi.blog.product.mapper.ProductMapper
import com.wutsi.blog.story.domain.StoryEntity
import com.wutsi.blog.story.mapper.StoryMapper
import com.wutsi.blog.user.domain.UserEntity
import com.wutsi.platform.core.image.Dimension
import com.wutsi.platform.core.image.ImageService
import com.wutsi.platform.core.image.Overlay
import com.wutsi.platform.core.image.OverlayType
import com.wutsi.platform.core.image.Transformation
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.text.DecimalFormat

@Service
class LinkMapper(
    private val storyMapper: StoryMapper,
    private val productMapper: ProductMapper,
    private val imageService: ImageService,
    @Value("\${wutsi.application.website-url}") private val webappUrl: String,
) {
    fun toLinkModel(story: StoryEntity, mailContext: MailContext, author: UserEntity? = null): LinkModel = LinkModel(
        title = story.title ?: "",
        url = mailContext.websiteUrl + storyMapper.slug(story),
        thumbnailUrl = story.thumbnailUrl?.let { url ->
            imageService.transform(
                url = url,
                transformation = Transformation(
                    dimension = Dimension(width = 400),
                    overlay = if (story.video == true) {
                        Overlay(
                            type = OverlayType.IMAGE,
                            input = "play.png",
                            dimension = Dimension(width = 64)
                        )
                    } else {
                        null
                    }
                )
            )
        },
        summary = story.summary,
        author = author?.fullName,
        authorPictureUrl = author?.pictureUrl?.let { url ->
            imageService.transform(url, Transformation(dimension = Dimension(width = 64)))
        },
        authorUrl = author?.let { "$webappUrl/@/${it.name}" },
    )

    fun toLinkModel(
        product: ProductEntity,
        offer: Offer?,
        mailContext: MailContext,
    ): LinkModel {
        val store = product.store
        val country = Country.all.find { it.currency.equals(store.currency, true) }
        val fmt = country?.monetaryFormat?.let { DecimalFormat(country.monetaryFormat) }

        val price = if (offer != null) {
            formatMoney(fmt, offer.price, store)
        } else {
            formatMoney(fmt, product.price, store)
        }

        val referencePrice = if (offer != null && offer.savingAmount > 0) {
            formatMoney(fmt, offer.referencePrice, store)
        } else {
            null
        }

        return LinkModel(
            title = product.title,
            url = mailContext.websiteUrl + productMapper.toSlug(product),
            thumbnailUrl = product.imageUrl?.let { url ->
                imageService.transform(url, Transformation(dimension = Dimension(height = 220)))
            },
            summary = if (referencePrice == null) {
                price
            } else {
                """
                    <b style="font-size:smaller">$price</b><br/>
                    <span style="text-decoration:line-through; font-size:smaller">$referencePrice</span>
                """.trimIndent()
            }
        )
    }

    private fun formatMoney(fmt: DecimalFormat?, amount: Long, store: StoreEntity): String =
        fmt?.format(amount) ?: "$amount ${store.currency}"
}
