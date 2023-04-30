package com.wutsi.editorjs.html.tag

import com.wutsi.editorjs.dom.Block
import com.wutsi.editorjs.dom.BlockData
import com.wutsi.editorjs.dom.BlockType
import com.wutsi.editorjs.dom.ListStyle
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.junit.jupiter.api.Test
import java.io.StringWriter
import kotlin.test.assertEquals

class ListTest {

    val tag = List()

    @Test
    fun writeUL() {
        val block = createBlock(listOf("Hello", "world"), ListStyle.unordered)
        val writer = StringWriter()

        tag.write(block, writer)

        assertEquals("<ul><li>Hello</li><li>world</li></ul>\n", writer.toString())
    }

    @Test
    fun writeOL() {
        val block = createBlock(listOf("Hello", "world"), ListStyle.ordered)
        val writer = StringWriter()

        tag.write(block, writer)

        assertEquals("<ol><li>Hello</li><li>world</li></ol>\n", writer.toString())
    }

    @Test
    fun writeWithStyle() {
        val block = createBlock(listOf("Hello", "wor<b>ld</b>"), ListStyle.ordered)
        val writer = StringWriter()

        tag.write(block, writer)

        assertEquals("<ol><li>Hello</li><li>wor<b>ld</b></li></ol>\n", writer.toString())
    }

    @Test
    fun readUL() {
        val elt = createElement("ul", listOf("Hello", "world"))
        val block = tag.read(elt)

        assertEquals(BlockType.list, block.type)
        assertEquals(2, block.data.items.size)
        assertEquals(ListStyle.unordered, block.data.style)
        assertEquals("Hello", block.data.items[0])
        assertEquals("world", block.data.items[1])
    }

    @Test
    fun readOL() {
        val elt = createElement("ol", listOf("Hello", "world"))
        val block = tag.read(elt)

        assertEquals(BlockType.list, block.type)
        assertEquals(2, block.data.items.size)
        assertEquals(ListStyle.ordered, block.data.style)
        assertEquals("Hello", block.data.items[0])
        assertEquals("world", block.data.items[1])
    }

    @Test
    fun readStyle() {
        val elt = createElement("ol", listOf("Hello", "wor<b>ld</b>"))
        val block = tag.read(elt)

        assertEquals(BlockType.list, block.type)
        assertEquals(2, block.data.items.size)
        assertEquals(ListStyle.ordered, block.data.style)
        assertEquals("Hello", block.data.items[0])
        assertEquals("wor<b>ld</b>", block.data.items[1])
    }

    private fun createBlock(items: kotlin.collections.List<String>, style: ListStyle) = Block(
        type = BlockType.list,
        data = BlockData(
            style = style,
            items = items
        )
    )

    private fun createElement(tag: String, items: kotlin.collections.List<String>): Element {
        val elt = Element(tag)
        items.forEach {
            val li = Element("li")
            val html = Jsoup.parse(it).body().html()
            li.html(html)

            elt.appendChild(li)
        }
        return elt
    }

}
