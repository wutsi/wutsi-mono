package com.wutsi.application.web.service.video

import org.springframework.stereotype.Service

@Service
class VideoProviderSet {
    private val providers = listOf(
        YouTubeProvider(),
        DailymotionProvider(),
        VimeoProvider(),
    )

    fun findProvider(url: String): VideoProvider? =
        providers.find { it.extractVideoId(url) != null }
}
