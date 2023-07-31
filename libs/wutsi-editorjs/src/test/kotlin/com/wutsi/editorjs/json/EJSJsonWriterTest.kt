package com.wutsi.editorjs.json

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.editorjs.ResourceHelper
import com.wutsi.editorjs.dom.Block
import com.wutsi.editorjs.dom.BlockData
import com.wutsi.editorjs.dom.BlockType
import com.wutsi.editorjs.dom.EJSDocument
import com.wutsi.editorjs.dom.File
import com.wutsi.editorjs.dom.Meta
import org.junit.jupiter.api.Test
import java.io.StringWriter
import kotlin.test.assertEquals

class EJSJsonWriterTest {
    private val mapper = ObjectMapper()

    @Test
    fun write() {
        val doc = createDocument()
        val expected = ResourceHelper.loadResourceAsString("/writer.json")

        val sw = StringWriter()
        val writer = EJSJsonWriter(mapper)
        writer.write(doc, sw)

        assertJsonEquals(expected, sw.toString())
    }

    private fun assertJsonEquals(expected: String, value: String) {
        val xexpected =
            mapper.writerWithDefaultPrettyPrinter().writeValueAsString(mapper.readValue(expected, Any::class.java))
        val xvalue =
            mapper.writerWithDefaultPrettyPrinter().writeValueAsString(mapper.readValue(value, Any::class.java))
        assertEquals(xexpected, xvalue)
    }

    private fun createDocument() = EJSDocument(
        blocks = arrayListOf(
            Block(
                type = BlockType.header,
                data = BlockData(
                    level = 1,
                    text = "Editor.js",
                ),
            ),
            Block(
                type = BlockType.paragraph,
                data = BlockData(
                    text = "Hey. Meet the new Editor. On this page you can see it in action — try to edit this text",
                ),
            ),
            Block(
                type = BlockType.list,
                data = BlockData(
                    items = arrayListOf(
                        "It is a block-styled editor",
                        "It returns clean data output in JSON",
                        "Designed to be extendable and pluggable with a simple API",
                    ),
                ),
            ),
            Block(
                type = BlockType.delimiter,
            ),
            Block(
                type = BlockType.image,
                data = BlockData(
                    caption = "Logo",
                    withBackground = true,
                    withBorder = true,
                    stretched = true,
                    file = File(
                        url = "/upload/temporary/o_488cfb382712d6af914301c73f376e8c.jpg",
                    ),
                ),
            ),
            Block(
                type = BlockType.code,
                data = BlockData(
                    code = "class Foo { }",
                ),
            ),
            Block(
                type = BlockType.linkTool,
                data = BlockData(
                    link = "https://www.afrohustler.com/3-personalities-we-should-express-henceforward-this-2020/",
                    meta = Meta(
                        title = "3 Personalities We Should Express Henceforward This 2020",
                        site_name = "www.afrohustler.com",
                        description = "As a businessperson and or employee, if you develop these 3 personalities, you will survive these trying times and come out stronger.",
                        image = File(
                            url = "https://www.afrohustler.com/wp-content/uploads/2020/05/3-Personalities-1110x398.jpg",
                        ),
                    ),
                ),
            ),
        ),
    )
}
