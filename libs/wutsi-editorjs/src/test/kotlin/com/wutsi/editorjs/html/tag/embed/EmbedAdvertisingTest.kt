package com.wutsi.editorjs.html.tag.embed

import com.wutsi.editorjs.dom.Block
import com.wutsi.editorjs.dom.BlockData
import com.wutsi.editorjs.dom.BlockType
import org.jsoup.nodes.Element
import org.junit.jupiter.api.Test
import java.io.StringWriter

class EmbedAdvertisingTest {
    val tag = EmbedAdvertising()

    @Test
    fun write() {
        val block = createBlock()
        val writer = StringWriter()

        tag.write(block, writer)

        kotlin.test.assertEquals(
            "<div class='ad'></div>",
            writer.toString(),
        )
    }

    @Test
    fun writeInvalidService() {
        val block = createBlock("xxx")
        val writer = StringWriter()

        tag.write(block, writer)

        kotlin.test.assertEquals("", writer.toString())
    }

    @Test
    fun read() {
        val elt = createElement()
        val block = tag.read(elt)

        kotlin.test.assertEquals(BlockType.embed, block?.type)
        kotlin.test.assertEquals(EmbedAdvertising.SERVICE, block?.data?.service)
    }

    private fun createBlock(service: String = EmbedAdvertising.SERVICE) = Block(
        type = BlockType.embed,
        data = BlockData(
            service = service,
        ),
    )

    private fun createElement(): Element {
        val elt = Element("div")
        elt.attr("class", EmbedAdvertising.CLASS)
        return elt
    }
}