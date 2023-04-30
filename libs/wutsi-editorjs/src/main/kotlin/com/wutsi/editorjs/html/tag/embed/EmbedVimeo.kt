package com.wutsi.editorjs.html.tag.embed

import java.util.regex.Matcher
import java.util.regex.Pattern

class EmbedVimeo: AbstractEmbedVideo() {
    private val pattern = Pattern.compile("[http|https]+:\\/\\/(?:www\\.|)vimeo\\.com\\/([a-zA-Z0-9_\\-]+)(&.+)?", Pattern.CASE_INSENSITIVE)

    override fun cssClass(): String  = "vimeo"

    override fun service(): String = "vimeo"

    override fun extractId(url: String): String {
        val matcher: Matcher = pattern.matcher(url)
        return if (matcher.find()) matcher.group(1) else ""
    }
}
