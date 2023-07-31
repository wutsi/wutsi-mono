package com.wutsi.editorjs.html.tag

import com.wutsi.editorjs.dom.BlockType
import org.jsoup.nodes.Element

class TagProvider {
    private val tagsByType = mapOf(
        BlockType.code to Code(),
        BlockType.raw to Code(),
        BlockType.delimiter to Delimiter(),
        BlockType.header to Header(),
        BlockType.image to Image(),
        BlockType.list to List(),
        BlockType.paragraph to Paragraph(),
        BlockType.quote to Quote(),
        BlockType.linkTool to Link(),
        BlockType.embed to Embed(),
        BlockType.button to Button(),
        BlockType.AnyButton to Button(),
        BlockType.attaches to Attaches(),
    )

    private val tagsByName = mapOf(
        "pre" to Code(),
        "hr" to Delimiter(),
        "h1" to Header(),
        "h2" to Header(),
        "h3" to Header(),
        "h4" to Header(),
        "h5" to Header(),
        "h6" to Header(),
        "img" to Image(),
        "figure" to Image(),
        "ul" to List(),
        "ol" to List(),
        "p" to Paragraph(),
        "blockquote" to Quote(),
        "a" to Link(),
        "div" to Embed(),
    )

    fun all() = tagsByType.values

    fun get(type: BlockType): Tag? = tagsByType.get(type)

    fun get(elt: Element): Tag? = tagsByName.get(elt.tagName())
}
