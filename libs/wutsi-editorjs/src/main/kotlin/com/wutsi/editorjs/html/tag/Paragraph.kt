package com.wutsi.editorjs.html.tag

import com.wutsi.editorjs.dom.Block
import com.wutsi.editorjs.dom.BlockData
import com.wutsi.editorjs.dom.BlockType
import org.jsoup.nodes.Element
import java.io.StringWriter

class Paragraph: Tag {
    override fun write (block: Block, writer: StringWriter) {
        val text = block.data.text

        writer.write("<p>$text</p>\n")
    }

    override fun read(elt: Element) = Block(
            type = BlockType.paragraph,
            data = BlockData(
                    text = elt.html()
            )
    )
}
