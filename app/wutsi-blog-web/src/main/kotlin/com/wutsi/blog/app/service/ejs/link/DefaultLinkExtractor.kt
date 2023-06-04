package com.wutsi.blog.app.service.ejs.link

import com.wutsi.blog.app.page.editor.model.EJSImageData
import com.wutsi.blog.app.page.editor.model.EJSLinkMeta
import com.wutsi.blog.app.service.LinkExtractor
import com.wutsi.extractor.DescriptionExtractor
import com.wutsi.extractor.Downloader
import com.wutsi.extractor.ImageExtractor
import com.wutsi.extractor.SiteNameExtractor
import com.wutsi.extractor.TitleExtractor
import org.springframework.stereotype.Service
import java.net.URL

@Service
class DefaultLinkExtractor(
    private val downloader: Downloader,
    private val imageExtractor: ImageExtractor,
    private val titleExtractor: TitleExtractor,
    private val descriptionExtractor: DescriptionExtractor,
    private val siteNameExtractor: SiteNameExtractor,
) : LinkExtractor {

    override fun accept(url: String) = true

    override fun extract(url: String): EJSLinkMeta {
        val html = downloader.download(URL(url))
        return EJSLinkMeta(
            title = nullToEmpty(titleExtractor.extract(html)),
            description = nullToEmpty(descriptionExtractor.extract(html)),
            site_name = nullToEmpty(siteNameExtractor.extract(URL(url), html)),
            image = EJSImageData(
                url = nullToEmpty(imageExtractor.extract(html)),
            ),
        )
    }

    private fun nullToEmpty(value: String?) = if (value == null) "" else value
}
