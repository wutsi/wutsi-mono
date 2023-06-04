package com.wutsi.blog.story.service

import com.wutsi.blog.story.dto.WebPage
import com.wutsi.extractor.ContentExtractor
import com.wutsi.extractor.Downloader
import com.wutsi.extractor.ImageExtractor
import com.wutsi.extractor.PublishedDateExtractor
import com.wutsi.extractor.SiteNameExtractor
import com.wutsi.extractor.TagExtractor
import com.wutsi.extractor.TitleExtractor
import com.wutsi.extractor.URLExtractor
import org.springframework.stereotype.Service
import java.net.URL

@Service
class WebScaperService(
    private val downloader: Downloader,
    private val contentExtractor: ContentExtractor,
    private val titleExtractor: TitleExtractor,
    private val tagExtractor: TagExtractor,
    private val siteNameExtractor: SiteNameExtractor,
    private val publishedDateExtractor: PublishedDateExtractor,
    private val imageExtractor: ImageExtractor,
    private val urlExtractor: URLExtractor,
) {
    fun scape(url: URL): WebPage {
        val html = downloader.download(url)
        val content = contentExtractor.extract(html)
        return WebPage(
            url = urlExtractor.extract(url, html),
            publishedDate = publishedDateExtractor.extract(html),
            tags = tagExtractor.extract(html),
            content = content,
            title = titleExtractor.extract(html),
            siteName = siteNameExtractor.extract(url, html),
            image = imageExtractor.extract(html),
        )
    }
}
