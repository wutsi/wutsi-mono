package com.wutsi.editorjs.html.tag

import com.wutsi.editorjs.dom.Block
import com.wutsi.editorjs.dom.BlockData
import com.wutsi.editorjs.dom.BlockType
import com.wutsi.editorjs.dom.ListStyle
import org.jsoup.nodes.Element
import java.io.StringWriter

class List: Tag {
    override fun write (block: Block, writer: StringWriter) {
        val name = if (block.data.style == ListStyle.unordered) "ul" else "ol"
        writer.write("<$name>")
        block.data.items.forEach { writer.write("<li>$it</li>") }
        writer.write("</$name>\n")
    }

    override fun read(elt: Element) =Block (
            type = BlockType.list,
            data = BlockData(
                    style = if ("ol" == elt.tagName().lowercase()) ListStyle.ordered else ListStyle.unordered,
                    items = elt.children().map { it.html() }
            )
    )
}
