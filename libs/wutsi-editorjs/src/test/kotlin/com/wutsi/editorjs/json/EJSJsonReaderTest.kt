package com.wutsi.editorjs.json

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.editorjs.ResourceHelper.loadResourceAsString
import com.wutsi.editorjs.dom.BlockType
import com.wutsi.editorjs.dom.ListItem
import com.wutsi.editorjs.dom.ListStyle
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class EJSJsonReaderTest {
    @Test
    fun read() {
        val json = loadResourceAsString("/reader.json")
        val reader = EJSJsonReader(ObjectMapper())
        val doc = reader.read(json)

        assertEquals(7, doc.blocks.size)

        assertEquals(BlockType.header, doc.blocks[0].type)
        assertEquals(1, doc.blocks[0].data.level)
        assertEquals("Editor.js", doc.blocks[0].data.text)

        assertEquals(BlockType.paragraph, doc.blocks[1].type)
        assertEquals(
            "Hey. Meet the new Editor. On this page you can see it in action — try to edit this text",
            doc.blocks[1].data.text,
        )

        assertEquals(BlockType.list, doc.blocks[2].type)
        assertEquals(ListStyle.unordered, doc.blocks[2].data.style)
        assertEquals(3, doc.blocks[2].data.items.size)
        assertEquals("It is a block-styled editor", (doc.blocks[2].data.items[0] as ListItem).content)
        assertEquals("It returns clean data output in JSON", (doc.blocks[2].data.items[1] as ListItem).content)
        assertEquals(
            "Designed to be extendable and pluggable with a simple API",
            (doc.blocks[2].data.items[2] as ListItem).content
        )

        assertEquals(BlockType.delimiter, doc.blocks[3].type)

        assertEquals(BlockType.image, doc.blocks[4].type)
        assertEquals("/upload/temporary/o_488cfb382712d6af914301c73f376e8c.jpg", doc.blocks[4].data.url)
        assertEquals("Logo", doc.blocks[4].data.caption)
        assertTrue(doc.blocks[4].data.withBackground)
        assertTrue(doc.blocks[4].data.withBorder)
        assertTrue(doc.blocks[4].data.stretched)

        assertEquals(BlockType.code, doc.blocks[5].type)
        assertEquals("class Foo { }", doc.blocks[5].data.code)

        assertEquals(BlockType.linkTool, doc.blocks[6].type)
        assertEquals(
            "https://www.afrohustler.com/3-personalities-we-should-express-henceforward-this-2020/",
            doc.blocks[6].data.link,
        )
        assertEquals("3 Personalities We Should Express Henceforward This 2020", doc.blocks[6].data.meta.title)
        assertEquals(
            "As a businessperson and or employee, if you develop these 3 personalities, you will survive these trying times and come out stronger.",
            doc.blocks[6].data.meta.description,
        )
        assertEquals("www.afrohustler.com", doc.blocks[6].data.meta.site_name)
        assertEquals(
            "https://www.afrohustler.com/wp-content/uploads/2020/05/3-Personalities-1110x398.jpg",
            doc.blocks[6].data.meta.image.url,
        )
    }

    @Test
    fun readSummary() {
        val json = loadResourceAsString("/reader.json")
        val reader = EJSJsonReader(ObjectMapper())
        val doc = reader.read(json, true)

        assertEquals(2, doc.blocks.size)

        assertEquals(BlockType.header, doc.blocks[0].type)
        assertEquals(1, doc.blocks[0].data.level)
        assertEquals("Editor.js", doc.blocks[0].data.text)

        assertEquals(BlockType.paragraph, doc.blocks[1].type)
        assertEquals(
            "Hey. Meet the new Editor. On this page you can see it in action — try to edit this text",
            doc.blocks[1].data.text,
        )
    }

    @Test
    fun readListItem() {
        val json = loadResourceAsString("/reader-list-item.json")
        val reader = EJSJsonReader(ObjectMapper())
        val doc = reader.read(json)

        assertEquals(7, doc.blocks.size)

        assertEquals(BlockType.list, doc.blocks[2].type)
        assertEquals(ListStyle.unordered, doc.blocks[2].data.style)
        assertEquals(3, doc.blocks[2].data.items.size)

        assertEquals("It is a block-styled editor", (doc.blocks[2].data.items[0] as ListItem).content)
        assertTrue((doc.blocks[2].data.items[0] as ListItem).items.isEmpty())

        assertEquals("It returns clean data output in JSON", (doc.blocks[2].data.items[1] as ListItem).content)
        assertTrue((doc.blocks[2].data.items[1] as ListItem).items.isEmpty())

        assertEquals(
            "Designed to be extendable and pluggable with a simple API",
            (doc.blocks[2].data.items[2] as ListItem).content
        )
        assertEquals(2, (doc.blocks[2].data.items[2] as ListItem).items.size)
    }
}
