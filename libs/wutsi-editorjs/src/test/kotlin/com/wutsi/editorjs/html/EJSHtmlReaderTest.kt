package com.wutsi.editorjs.html

import com.wutsi.editorjs.ResourceHelper.loadResourceAsString
import com.wutsi.editorjs.dom.BlockType
import com.wutsi.editorjs.dom.ListStyle
import com.wutsi.editorjs.html.tag.TagProvider
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class EJSHtmlReaderTest {
    @Test
    fun read() {
        val html = loadResourceAsString("/reader.html")
        val reader = EJSHtmlReader(TagProvider())
        val doc = reader.read(html)

        assertEquals(7, doc.blocks.size)

        assertEquals(BlockType.header, doc.blocks[0].type)
        assertEquals(1, doc.blocks[0].data.level)
        assertEquals("Editor.js", doc.blocks[0].data.text)

        assertEquals(BlockType.paragraph, doc.blocks[1].type)
        assertEquals("Hey. Meet the new Editor. On this page you can see it in action — try to edit this text",
            doc.blocks[1].data.text)

        assertEquals(BlockType.list, doc.blocks[2].type)
        assertEquals(ListStyle.unordered, doc.blocks[2].data.style)
        assertEquals(3, doc.blocks[2].data.items.size)
        assertEquals("It is a block-styled editor", doc.blocks[2].data.items[0])
        assertEquals("It returns clean data output in JSON", doc.blocks[2].data.items[1])
        assertEquals("Designed to be extendable and pluggable with a simple API", doc.blocks[2].data.items[2])

        assertEquals(BlockType.delimiter, doc.blocks[3].type)

        assertEquals(BlockType.image, doc.blocks[4].type)
        assertEquals("/upload/temporary/o_488cfb382712d6af914301c73f376e8c.jpg", doc.blocks[4].data.file.url)
        assertEquals(100, doc.blocks[4].data.file.width)
        assertEquals(80, doc.blocks[4].data.file.height)
        assertEquals("Logo", doc.blocks[4].data.caption)
        assertTrue(doc.blocks[4].data.withBackground)
        assertTrue(doc.blocks[4].data.withBorder)
        assertTrue(doc.blocks[4].data.stretched)

        assertEquals(BlockType.code, doc.blocks[5].type)
        assertEquals("class Foo { }", doc.blocks[5].data.code)

        assertEquals(BlockType.linkTool, doc.blocks[6].type)
        assertEquals("https://www.afrohustler.com/3-personalities-we-should-express-henceforward-this-2020/",
            doc.blocks[6].data.link)
        assertEquals("3 Personalities We Should Express Henceforward This 2020", doc.blocks[6].data.meta.title)
        assertEquals("As a businessperson and or employee, if you develop these 3 personalities, you will survive these trying times and come out stronger.",
            doc.blocks[6].data.meta.description)
        assertEquals("www.afrohustler.com", doc.blocks[6].data.meta.site_name)
        assertEquals("https://www.afrohustler.com/wp-content/uploads/2020/05/3-Personalities-1110x398.jpg",
            doc.blocks[6].data.meta.image.url)
    }

    @Test
    fun article() {
        val html = loadResourceAsString("/article.html")
        val reader = EJSHtmlReader(TagProvider())
        val doc = reader.read(html)
        assertEquals(32, doc.blocks.size)
    }
}
