package com.wutsi.application.web.service.video

import java.util.regex.Pattern

class YouTubeProvider : VideoProvider {
    companion object {
        private val VIDEO_URL_PATTERNS = arrayOf(
            "youtu\\.be\\/([a-zA-Z0-9-]*)(\\?.*)?",
            "youtube(\\-nocookie)?\\.com\\/embed\\/([a-zA-Z0-9-]*)(\\?.*)?",
            "youtube(\\-nocookie)?\\.com\\/watch\\?v=([a-zA-Z0-9-]*)(\\&.*)?",
        )
        private val VIDEO_GROUP_INDEX = intArrayOf(1, 2, 2)
        private const val EMBED_URL_FORMAT = "https://www.youtube.com/embed/%s"
    }

    // -- VideoProvider overrides
    override fun generateEmbedUrl(videoId: String): String {
        return String.format(EMBED_URL_FORMAT, videoId)
    }

    override fun extractVideoId(url: String): String? {
        if (url.contains("youtube.com/user/")) {
            return null
        } else {
            for (i in VIDEO_URL_PATTERNS.indices) {
                val pattern = VIDEO_URL_PATTERNS[i]
                val group = VIDEO_GROUP_INDEX[i]
                val compiledPattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE)
                val matcher = compiledPattern.matcher(url)
                while (matcher.find()) {
                    return matcher.group(group)
                }
            }
        }
        return null
    }
}
