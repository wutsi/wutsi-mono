package com.wutsi.editorjs.html.tag

import com.wutsi.editorjs.dom.Block
import com.wutsi.editorjs.dom.BlockData
import com.wutsi.editorjs.dom.BlockType
import com.wutsi.editorjs.dom.ListItem
import com.wutsi.editorjs.dom.ListStyle
import org.jsoup.nodes.Element
import java.io.StringWriter

class List : Tag {
    override fun write(block: Block, writer: StringWriter) {
        val name = if (block.data.style == ListStyle.unordered) "ul" else "ol"
        writer.write("<$name>")
        block.data.items.forEach { item ->
            writeItems(name, item, writer)
        }
        writer.write("</$name>\n")
    }

    private fun writeItems(name: String, item: Any, writer: StringWriter) {
        if (item is String) {
            writer.write("<li>$item</li>")
        } else if (item is ListItem) { // List Item
            writeListItem(name, item, writer)
        }
    }

    private fun writeListItem(name: String, item: ListItem, writer: StringWriter) {
        writer.write("<li>")

        writer.write(item.content)
        if (item.items.isNotEmpty()) {
            writer.write("<$name>")
            item.items.forEach { item -> writeItems(name, item, writer) }
            writer.write("</$name>")
        }

        writer.write("</li>")
    }

    override fun read(elt: Element) = Block(
        type = BlockType.list,
        data = BlockData(
            style = if ("ol" == elt.tagName().lowercase()) ListStyle.ordered else ListStyle.unordered,
            items = elt.children().map { it.html() },
        ),
    )
}
