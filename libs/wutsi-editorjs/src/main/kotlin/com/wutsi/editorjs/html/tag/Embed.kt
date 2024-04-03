package com.wutsi.editorjs.html.tag

import com.wutsi.editorjs.dom.Block
import com.wutsi.editorjs.html.tag.embed.EmbedAdvertising
import com.wutsi.editorjs.html.tag.embed.EmbedTwitter
import com.wutsi.editorjs.html.tag.embed.EmbedVimeo
import com.wutsi.editorjs.html.tag.embed.EmbedYouTube
import org.jsoup.nodes.Element
import java.io.StringWriter

class Embed : Tag {
    val delegates: Map<String, Tag> = mapOf(
        EmbedTwitter.SERVICE to EmbedTwitter(),
        EmbedYouTube.SERVICE to EmbedYouTube(),
        EmbedVimeo.SERVICE to EmbedVimeo(),
        EmbedAdvertising.SERVICE to EmbedAdvertising()
    )

    override fun write(block: Block, writer: StringWriter) {
        delegates.values.forEach {
            val tmp = StringWriter()
            it.write(block, tmp)
            if (!tmp.toString().isEmpty()) {
                writer.write(tmp.toString())
                return
            }
        }
    }

    override fun read(elt: Element): Block? {
        delegates.values.forEach {
            val block = it.read(elt)
            if (block != null) {
                return block
            }
        }
        return null
    }
}
