package com.wutsi.editorjs.html.tag.embed

import com.wutsi.editorjs.dom.Block
import com.wutsi.editorjs.dom.BlockData
import com.wutsi.editorjs.dom.BlockType
import com.wutsi.editorjs.html.tag.Tag
import org.jsoup.nodes.Element
import java.io.StringWriter

class EmbedAdvertising : Tag {
    companion object {
        const val SERVICE = "advertising"
        const val CLASS = "ad"
    }

    override fun write(block: Block, writer: StringWriter) {
        if (block.data.service != SERVICE) {
            return
        }

        writer.write("<div class='$CLASS'></div>")
    }

    override fun read(elt: Element): Block? {
        val clazz = elt.attr("class")
        if (clazz != CLASS) {
            return null
        }

        return Block(
            type = BlockType.embed,
            data = BlockData(
                service = SERVICE,
            ),
        )
    }
}
