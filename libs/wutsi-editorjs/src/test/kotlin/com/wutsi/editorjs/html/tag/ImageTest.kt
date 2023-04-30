package com.wutsi.editorjs.html.tag

import com.wutsi.editorjs.dom.Block
import com.wutsi.editorjs.dom.BlockData
import com.wutsi.editorjs.dom.BlockType
import com.wutsi.editorjs.dom.File
import org.jsoup.nodes.Element
import org.junit.jupiter.api.Test
import java.io.StringWriter
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ImageTest {
    val tag = Image()

    @Test
    fun writeSimpleImage() {
        val block = createBlock("http://www.img.com/1.png", width = -1, height = -1)
        block.data.url = block.data.file.url
        block.data.file.url = ""
        val writer = StringWriter()

        tag.write(block, writer)

        assertEquals("<figure><img src='http://www.img.com/1.png' /></figure>\n", writer.toString())
    }


    @Test
    fun writeUrl() {
        val block = createBlock("http://www.img.com/1.png")
        val writer = StringWriter()

        tag.write(block, writer)

        assertEquals("<figure><img src='http://www.img.com/1.png' width=100 height=80 /></figure>\n", writer.toString())
    }

    @Test
    fun writeWithCaption() {
        val block = createBlock(url = "http://www.img.com/1.png", caption = "foo")
        val writer = StringWriter()

        tag.write(block, writer)

        assertEquals("<figure><img src='http://www.img.com/1.png' alt='foo' width=100 height=80 /><figcaption>foo</figcaption></figure>\n",
            writer.toString())
    }

    @Test
    fun writeWithBorder() {
        val block = createBlock(url = "http://www.img.com/1.png", border = true)
        val writer = StringWriter()

        tag.write(block, writer)

        assertEquals("<figure><img src='http://www.img.com/1.png' class='border' width=100 height=80 /></figure>\n",
            writer.toString())
    }

    @Test
    fun writeNoDimensions() {
        val block = createBlock(url = "http://www.img.com/1.png", width = -1, height = -1)
        val writer = StringWriter()

        tag.write(block, writer)

        assertEquals("<figure><img src='http://www.img.com/1.png' /></figure>\n", writer.toString())
    }

    @Test
    fun writeWithBackground() {
        val block = createBlock(url = "http://www.img.com/1.png", background = true)
        val writer = StringWriter()

        tag.write(block, writer)

        assertEquals("<figure><img src='http://www.img.com/1.png' class='background' width=100 height=80 /></figure>\n",
            writer.toString())
    }

    @Test
    fun writeStretched() {
        val block = createBlock(url = "http://www.img.com/1.png", stretched = true)
        val writer = StringWriter()

        tag.write(block, writer)

        assertEquals("<figure><img src='http://www.img.com/1.png' class='stretched' width=100 height=80 /></figure>\n",
            writer.toString())
    }

    @Test
    fun writeAll() {
        val block = createBlock(url = "http://www.img.com/1.png",
            caption = "foo",
            stretched = true,
            background = true,
            border = true)
        val writer = StringWriter()

        tag.write(block, writer)

        assertEquals("<figure><img src='http://www.img.com/1.png' alt='foo' class='stretched border background' width=100 height=80 /><figcaption>foo</figcaption></figure>\n",
            writer.toString())
    }

    @Test
    fun readImage() {
        val elt = createIMGElement("http://www.google.com/1.png", "test", 111, 333)
        val block = tag.read(elt)

        assertEquals(BlockType.image, block?.type)
        assertEquals("http://www.google.com/1.png", block?.data?.url)
        assertEquals("http://www.google.com/1.png", block?.data?.file?.url)
        assertEquals(111, block?.data?.file?.width)
        assertEquals(333, block?.data?.file?.height)
        assertEquals("test", block?.data?.caption)
        assertEquals(true, block?.data?.withBackground)
        assertEquals(true, block?.data?.withBackground)
        assertEquals(true, block?.data?.stretched)
    }


    @Test
    fun readImageNoURL() {
        val elt = createIMGElement("", "test", 111, 333)
        val block = tag.read(elt)

        assertNull(block)
    }

    @Test
    fun readFigure() {
        val elt = createFigureElement("http://www.google.com/1.png", "test")
        val block = tag.read(elt)

        assertEquals(BlockType.image, block?.type)
        assertEquals("http://www.google.com/1.png", block?.data?.url)
        assertEquals("http://www.google.com/1.png", block?.data?.file?.url)
        assertEquals("test", block?.data?.caption)
        assertEquals(true, block?.data?.withBackground)
        assertEquals(true, block?.data?.withBackground)
        assertEquals(true, block?.data?.stretched)
    }

    @Test
    fun readFigureNoUrl() {
        val elt = createFigureElement("", "test")
        val block = tag.read(elt)

        assertNull(block)
    }

    private fun createBlock(
        url: String,
        caption: String = "",
        stretched: Boolean = false,
        background: Boolean = false,
        border: Boolean = false,
        width: Int = 100,
        height: Int = 80,
    ) = Block(
        type = BlockType.image,
        data = BlockData(
            caption = caption,
            stretched = stretched,
            withBorder = border,
            withBackground = background,
            file = File(
                url = url,
                width = width,
                height = height
            )
        )
    )


    private fun createIMGElement(url: String, alt: String, width: Int = -1, height: Int = -1): Element {
        val elt = Element("img")
        elt.attr("src", url)
        elt.attr("alt", alt)
        elt.attr("width", width.toString())
        elt.attr("height", height.toString())
        elt.addClass("stretched")
        elt.addClass("background")
        elt.addClass("border")
        return elt
    }

    private fun createFigureElement(url: String, alt: String): Element {
        val elt = Element("figure")

        val caption = Element("figcaption")
        caption.text(alt)

        elt.appendChild(createIMGElement(url, alt))
        elt.appendChild(caption)
        return elt
    }

}
