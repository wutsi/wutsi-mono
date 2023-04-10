package com.wutsi.application.web.service.video

import java.util.regex.Pattern

class VimeoProvider : VideoProvider {
    companion object {
        private val VIDEO_URL_PATTERN = Pattern.compile("vimeo\\.com.*/([0-9]+)$", Pattern.CASE_INSENSITIVE)
        private const val EMBED_URL_FORMAT = "https://player.vimeo.com/video/%s"
    }

    override fun generateEmbedUrl(videoId: String): String =
        String.format(EMBED_URL_FORMAT, videoId)

    override fun extractVideoId(url: String): String? {
        val matcher = VIDEO_URL_PATTERN.matcher(url)
        while (matcher.find()) {
            return matcher.group(1)
        }
        return null
    }
}
