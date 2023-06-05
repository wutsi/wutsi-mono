package com.wutsi.blog.app.service.ejs.link

import com.wutsi.blog.app.page.admin.model.EJSImageData
import com.wutsi.blog.app.page.admin.model.EJSLinkMeta
import com.wutsi.blog.app.service.LinkExtractor
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.util.regex.Matcher
import java.util.regex.Pattern

@Service
class YouTubeLinkExtractor(
    private val rest: RestTemplate,
    @Value("\${wutsi.oauth.google.api-key}") private val apiKey: String,
) : LinkExtractor {
    override fun accept(url: String) = videoId(url) != null

    override fun extract(url: String): EJSLinkMeta {
        val id = videoId(url)
        val yturl = "https://www.googleapis.com/youtube/v3/videos?id=$id&key=$apiKey&part=snippet"
        val response = rest.getForEntity(yturl, YTListResponse::class.java).body
        val video = response!!.items[0]
        return EJSLinkMeta(
            title = video.snippet.title,
            description = "",
            site_name = "YouTube",
            image = EJSImageData(
                url = nullToEmpty(video.snippet.thumbnails?.standard?.url),
            ),
        )
    }

    private fun videoId(url: String): String? {
        val pattern =
            "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%\u200C\u200B2F|youtu.be%2F|%2Fv%2F)[^#\\&\\?\\n]*"

        val compiledPattern: Pattern = Pattern.compile(pattern)
        val matcher: Matcher = compiledPattern.matcher(url)

        return if (matcher.find()) matcher.group() else null
    }

    private fun nullToEmpty(value: String?) = if (value == null) "" else value
}

data class YTListResponse(
    val kind: String,
    val etag: String,
    val items: List<YTVideo> = emptyList(),
)

data class YTVideo(
    val id: String,
    val snippet: YTSnippet,
)

data class YTSnippet(
    val title: String,
    val description: String = "",
    val channelTitle: String,
    val thumbnails: YTThumbnails? = null,
)

data class YTThumbnails(
    val standard: YTAsset? = null,
    val default: YTAsset? = null,
    val medium: YTAsset? = null,
    val hight: YTAsset? = null,
)

data class YTAsset(
    val url: String = "",
)
