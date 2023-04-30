package com.wutsi.editorjs.html.tag

import com.wutsi.editorjs.dom.Block
import com.wutsi.editorjs.dom.BlockType
import org.jsoup.nodes.Element
import org.junit.jupiter.api.Test
import java.io.StringWriter
import kotlin.test.assertEquals

class DelimiterTest {

    val tag = Delimiter()

    @Test
    fun write() {
        val block = createBlock()
        val writer = StringWriter()

        tag.write(block, writer)

        assertEquals("<hr />\n", writer.toString())
    }

    @Test
    fun read() {
        val elt = createElement()
        val block = tag.read(elt)

        assertEquals(BlockType.delimiter, block.type)
    }

    private fun createBlock() = Block(
        type = BlockType.delimiter,
    )

    private fun createElement() = Element("hr")
}
