package com.wutsi.editorjs.html.tag

import com.wutsi.editorjs.dom.Block
import com.wutsi.editorjs.dom.BlockData
import com.wutsi.editorjs.dom.BlockType
import org.jsoup.nodes.Element
import java.io.StringWriter

class Header: Tag {
    override fun write (block: Block, writer: StringWriter) {
        val text = block.data.text
        val name = "h${block.data.level}"
        writer.write("<$name>$text</$name>\n")
    }

    override fun read(elt: Element) = Block(
            type = BlockType.header,
            data = BlockData(
                    text = elt.text(),
                    level = elt.tagName().substring(1).toInt()
            )
    )
}
