package com.wutsi.application.web.service.video

interface VideoProvider {
    fun extractVideoId(url: String): String?
    fun generateEmbedUrl(videoId: String): String
}
