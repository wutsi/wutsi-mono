package com.wutsi.blog.mail.service.sender.story

import com.wutsi.blog.ads.dto.AdsCTAType
import com.wutsi.blog.mail.mapper.AdsMapper
import com.wutsi.blog.mail.service.model.AdsModel
import com.wutsi.blog.story.domain.StoryEntity
import com.wutsi.blog.user.domain.UserEntity
import com.wutsi.editorjs.dom.Block
import com.wutsi.editorjs.dom.BlockData
import com.wutsi.editorjs.dom.BlockType
import com.wutsi.editorjs.dom.EJSDocument
import com.wutsi.editorjs.html.tag.embed.EmbedAdvertising
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.springframework.context.MessageSource
import org.springframework.stereotype.Service
import java.util.Locale

@Service
class AdsEJSFilter(
    private val adsMapper: AdsMapper,
    private val message: MessageSource,
) {
    /**
     * Add the ads at position 1/3
     */
    fun filter(doc: EJSDocument) {
        val index1 = doc.blocks.size * .3
        if (index1 > 3) {
            insertAt(index1, doc)
        }

        val index2 = doc.blocks.size * .6
        if (index2 > index1 + 3) {
            insertAt(index2, doc)
        }

        val index3 = doc.blocks.size * .9
        if (index3 > index2 + 3) {
            insertAt(index3, doc)
        }
    }

    fun filter(html: String, ads: List<AdsModel>, recipient: UserEntity, story: StoryEntity, language: Locale): String {
        val doc = Jsoup.parse(html)
        var i = 0
        doc.select("div.ad")
            .forEach {
                filter(it, ads[i], recipient, story, language)
                i = (i + 1) % ads.size
            }
        return doc.html()
    }

    private fun filter(
        elt: Element,
        banner: AdsModel,
        recipient: UserEntity,
        story: StoryEntity,
        language: Locale,
    ) {
        val pixelUrl = adsMapper.getAdsPixelUrl(banner, recipient, story)
        val title = message.getMessage("label.ads", emptyArray(), language).uppercase()
        val cta = if (banner.ctaType == AdsCTAType.UNKNOWN) {
            ""
        } else {
            val button = message.getMessage("ads.cta." + banner.ctaType.name, emptyArray(), language)
            """
                <div class="padding-small text-center">
                    <a class="btn btn-secondary" target="_new" href="${banner.ctaUrl}">$button</a>
                </div>                        
            """.trimIndent()
        }

        elt.html(
            """
                <img src="$pixelUrl"/>
                <div class="padding" th:fragment="banner(banner, pixelUrl)">
                    <div class="border ads-container" style="width: ${banner.type.width}px">
                        <div>$title</div>
                        <div>
                            <a target="_new" href="${banner.url}">
                                <img height="${banner.type.height}" src="${banner.imageUrl}" width="${banner.type.width}" />
                            </a>
                        </div>
                        $cta
                    </div>
                </div>
                
            """.trimIndent()
        )
    }

    private fun insertAt(index: Double, doc: EJSDocument) {
        doc.blocks.add(
            index.toInt(),
            Block(
                type = BlockType.embed,
                data = BlockData(
                    service = EmbedAdvertising.SERVICE,
                ),
            ),
        )
    }
}
