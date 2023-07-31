package com.wutsi.editorjs.html.tag

import com.wutsi.editorjs.dom.Block
import com.wutsi.editorjs.dom.BlockData
import com.wutsi.editorjs.dom.BlockType.attaches
import com.wutsi.editorjs.dom.File
import org.junit.jupiter.api.Test
import java.io.StringWriter
import kotlin.test.assertEquals

class LinkTest {
    val tag = Link()

    @Test
    fun writeCode() {
        val block = createBlock("https://www.google.ca/img.png", "img.png", "png")
        val writer = StringWriter()

        tag.write(block, writer)

        assertEquals(
            "<div class='button stretched centered large'><a href='https://www.google.ca'>Hello world</a></div>\n",
            writer.toString(),
        )
    }

    private fun createBlock(link: String, name: String, extension: String, size: Long) = Block(
        type = attaches,
        data = BlockData(
            file = File(
                url = link,
                name = name,
                extension = extension,
                size = size
            )
        ),
    )
}
