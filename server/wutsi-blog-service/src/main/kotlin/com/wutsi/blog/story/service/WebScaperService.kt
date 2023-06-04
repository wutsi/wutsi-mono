package com.wutsi.blog.tools.service

import com.wutsi.blog.client.extractor.ExtractWebPageResponse
import com.wutsi.blog.client.extractor.WebPageDto
import com.wutsi.blog.story.service.EditorJSService
import com.wutsi.core.exception.NotFoundException
import com.wutsi.extractor.ContentExtractor
import com.wutsi.extractor.Downloader
import com.wutsi.extractor.ImageExtractor
import com.wutsi.extractor.PublishedDateExtractor
import com.wutsi.extractor.SiteNameExtractor
import com.wutsi.extractor.TagExtractor
import com.wutsi.extractor.TitleExtractor
import com.wutsi.extractor.URLExtractor
import com.wutsi.extractor.rss.Item
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
    private val editorJSService: EditorJSService

) {
    fun extract(url: URL): ExtractWebPageResponse {
        val html = download(url)
        val content = contentExtractor.extract(html)
        return ExtractWebPageResponse(
            page = WebPageDto(
                url = urlExtractor.extract(url, html),
                publishedDate = publishedDateExtractor.extract(html),
                tags = tagExtractor.extract(html),
                content = content,
                title = titleExtractor.extract(html),
                siteName = siteNameExtractor.extract(url, html),
                image = imageExtractor.extract(html)
            ),
            editorjs = editorJSService.fromHtml(content)
        )
    }

    fun extract(item: Item): ExtractWebPageResponse {
        val url = URL(item.link)
        val html = download(url)
        val content = contentExtractor.extract(html)
        return ExtractWebPageResponse(
            page = WebPageDto(
                url = item.link,
                publishedDate = item.publishedDate,
                tags = item.categories,
                content = content,
                title = item.title,
                siteName = siteNameExtractor.extract(url, html),
                image = imageExtractor.extract(html)
            ),
            editorjs = editorJSService.fromHtml(content)
        )
    }

    private fun download(url: URL): String {
        try {
            return downloader.download(url)
        } catch (ex: Exception) {
            throw NotFoundException("story_not_found", ex)
        }
    }
}
