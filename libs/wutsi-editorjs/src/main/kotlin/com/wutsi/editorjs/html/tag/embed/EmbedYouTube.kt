package com.wutsi.editorjs.html.tag.embed

import java.util.regex.Matcher
import java.util.regex.Pattern

class EmbedYouTube : AbstractEmbedVideo() {
    companion object {
        const val SERVICE = "youtube"
    }

    private val pattern =
        Pattern.compile(
            "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%\u200C\u200B2F|youtu.be%2F|%2Fv%2F)[^#\\&\\?\\n]*",
            Pattern.CASE_INSENSITIVE,
        )

    override fun getDisplayName(): String = "YouTube"

    override fun cssClass(): String = "youtube"

    override fun service(): String = SERVICE

    override fun extractId(url: String): String {
        val matcher: Matcher = pattern.matcher(url)
        return if (matcher.find()) matcher.group() else ""
    }

    override fun getImageUrl(id: String): String =
        "https://i.ytimg.com/vi/$id/maxresdefault.jpg"
}
