package com.wutsi.editorjs.html.tag

import com.wutsi.editorjs.dom.Block
import com.wutsi.editorjs.dom.BlockType
import org.jsoup.nodes.Element
import java.io.StringWriter

class Delimiter: Tag {
    override fun write (block: Block, writer: StringWriter) {
        writer.write("<hr />\n")
    }

    override fun read(elt: Element) = Block(
            type = BlockType.delimiter
    )
}
