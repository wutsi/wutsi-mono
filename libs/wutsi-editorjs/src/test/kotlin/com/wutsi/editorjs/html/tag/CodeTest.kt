package com.wutsi.editorjs.html.tag

import com.wutsi.editorjs.dom.Block
import com.wutsi.editorjs.dom.BlockData
import com.wutsi.editorjs.dom.BlockType
import org.jsoup.nodes.Element
import org.junit.jupiter.api.Test
import java.io.StringWriter
import kotlin.test.assertEquals

class CodeTest {

    val tag = Code()

    @Test
    fun writeCode() {
        val block = createBlock("Hello world", BlockType.code)
        val writer = StringWriter()

        tag.write(block, writer)

        assertEquals("<pre class='code'>Hello world</pre>\n", writer.toString())
    }

    @Test
    fun readCode() {
        val elt = createElement("yo", "code")
        val block = tag.read(elt)

        assertEquals(BlockType.code, block?.type)
        assertEquals("yo", block?.data?.code)
        assertEquals("", block?.data?.html)
    }

    @Test
    fun writeHTML() {
        val block = createBlock("<b>Hello</b> world", BlockType.raw)
        val writer = StringWriter()

        tag.write(block, writer)

        assertEquals("<pre class='raw'>&lt;b&gt;Hello&lt;/b&gt; world</pre>\n", writer.toString())
    }

    @Test
    fun readHTML() {
        val elt = createElement("<b>Hello</b> world", "raw")
        val block = tag.read(elt)

        assertEquals(BlockType.raw, block?.type)
        assertEquals("<b>Hello</b> world", block?.data?.html)
        assertEquals("", block?.data?.code)
    }


    private fun createBlock(code: String, type: BlockType) = Block(
        type = type,
        data = BlockData(
            code = if (type == BlockType.code) code else "",
            html = if (type == BlockType.raw) code else ""
        )
    )

    private fun createElement(code: String, clazz: String): Element {
        val elt = Element("code")
        elt.html(code)
        elt.addClass(clazz)
        return elt
    }
}
