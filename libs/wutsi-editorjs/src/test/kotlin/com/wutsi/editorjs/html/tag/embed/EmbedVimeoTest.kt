package com.wutsi.editorjs.html.tag.embed

import com.wutsi.editorjs.dom.Block
import com.wutsi.editorjs.dom.BlockData
import com.wutsi.editorjs.dom.BlockType
import org.jsoup.nodes.Element
import org.junit.jupiter.api.Test
import java.io.StringWriter
import kotlin.test.assertEquals

class EmbedVimeoTest {
    val tag = EmbedVimeo()

    @Test
    fun write() {
        val block = createBlock("1264718256809656320", "Yo")
        val writer = StringWriter()

        tag.write(block, writer)

        assertEquals(
            "<div class='vimeo' data-id='1264718256809656320' data-source='https://www.vimeo.com/1264718256809656320' data-width='600' data-height='320' data-caption='Yo'><div id='vimeo-1264718256809656320' class='player'></div></div>\n",
            writer.toString(),
        )
    }

    @Test
    fun writeInvalidService() {
        val block = createBlock("1264718256809656320", "Yo", "????")
        val writer = StringWriter()

        tag.write(block, writer)

        assertEquals("", writer.toString())
    }

    @Test
    fun read() {
        val elt = createElement("1264718256809656320", "man")
        val block = tag.read(elt)

        assertEquals(BlockType.embed, block?.type)
        assertEquals("vimeo", block?.data?.service)
        assertEquals("555", block?.data?.width)
        assertEquals("200", block?.data?.height)
        assertEquals("man", block?.data?.caption)
        assertEquals("https://twitframe.com/show?url=https://www.vimeo.com/1264718256809656320", block?.data?.embed)
        assertEquals("https://www.vimeo.com/1264718256809656320", block?.data?.source)
    }

    private fun createBlock(id: String, caption: String, service: String = "vimeo") = Block(
        type = BlockType.embed,
        data = BlockData(
            service = service,
            caption = caption,
            source = "https://www.vimeo.com/$id",
            embed = "https://twitframe.com/show?url=https://www.vimeo.com/$id",
            width = "600",
            height = "320",
        ),
    )

    private fun createElement(id: String, caption: String): Element {
        val elt = Element("div")
        elt.attr("id", "vimeo-$id")
        elt.attr("class", "vimeo")
        elt.attr("data-height", "200")
        elt.attr("data-width", "555")
        elt.attr("data-id", id)
        elt.attr("data-source", "https://www.vimeo.com/$id")
        elt.attr("data-caption", caption)
        return elt
    }
}
