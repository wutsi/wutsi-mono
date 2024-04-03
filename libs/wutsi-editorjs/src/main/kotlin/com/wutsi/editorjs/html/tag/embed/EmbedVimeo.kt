package com.wutsi.editorjs.html.tag.embed

import java.util.regex.Matcher
import java.util.regex.Pattern

class EmbedVimeo : AbstractEmbedVideo() {
    companion object {
        const val SERVICE = "vimeo"
    }

    private val pattern = Pattern.compile(
        "[http|https]+:\\/\\/(?:www\\.|)vimeo\\.com\\/([a-zA-Z0-9_\\-]+)(&.+)?",
        Pattern.CASE_INSENSITIVE,
    )

    override fun getDisplayName(): String = "Vimeo"

    override fun cssClass(): String = SERVICE

    override fun service(): String = "vimeo"

    override fun extractId(url: String): String {
        val matcher: Matcher = pattern.matcher(url)
        return if (matcher.find()) matcher.group(1) else ""
    }

    override fun getImageUrl(id: String): String =
        "https://vumbnail.com/${id}_large.jpg"
}
