package com.wutsi.editorjs.html.tag

import com.wutsi.editorjs.dom.Block
import com.wutsi.editorjs.dom.BlockData
import com.wutsi.editorjs.dom.BlockType.button
import org.jsoup.nodes.Element
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import java.io.StringWriter
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ButtonTest {
    val tag = Button()

    @Test
    fun writeCode() {
        val block = createBlock("https://www.google.ca", "Hello world")
        val writer = StringWriter()

        tag.write(block, writer)

        assertEquals(
            "<div class='button stretched centered large'><a href='https://www.google.ca'>Hello world</a></div>\n",
            writer.toString(),
        )
    }

    @Test
    fun readHTML() {
        val elt = mock(Element::class.java)
        val block = tag.read(elt)

        assertNull(block)
    }

    private fun createBlock(link: String, text: String) = Block(
        type = button,
        data = BlockData(
            url = link,
            label = text,
            stretched = true,
            centered = true,
            large = true,
        ),
    )
}
