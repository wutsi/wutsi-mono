package com.wutsi.editorjs.html.tag.embed

import com.wutsi.editorjs.dom.Block
import com.wutsi.editorjs.dom.BlockData
import com.wutsi.editorjs.dom.BlockType
import com.wutsi.editorjs.html.tag.Tag
import org.apache.commons.text.StringEscapeUtils
import org.jsoup.nodes.Element
import java.io.StringWriter
import java.net.URL

class EmbedTwitter : Tag {
    override fun write(block: Block, writer: StringWriter) {
        if (block.data.service != "twitter") {
            return
        }

        val width = block.data.width
        val height = block.data.height
        val source = block.data.source
        val caption = StringEscapeUtils.escapeHtml4(block.data.caption)
        val id = extractId(source)
        writer.write("<div class='tweet' data-id='$id' data-source='$source' data-width='$width' data-height='$height' data-caption='$caption'></div>\n")
    }

    override fun read(elt: Element): Block? {
        val clazz = elt.attr("class")
        val id = elt.attr("data-id")
        if (id.isEmpty() || clazz != "tweet") {
            return null
        }

        return Block(
            type = BlockType.embed,
            data = BlockData(
                caption = elt.attr("data-caption"),
                width = elt.attr("data-width"),
                height = elt.attr("data-height"),
                source = elt.attr("data-source"),
                embed = "https://twitframe.com/show?url=" + elt.attr("data-source"),
                service = "twitter",
            ),
        )
    }

    private fun extractId(value: String): String {
        val url = URL(value)
        val i = url.path.lastIndexOf('/')
        return url.path.substring(i + 1)
    }
}
