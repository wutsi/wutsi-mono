package com.wutsi.editorjs.html.tag

import com.wutsi.editorjs.dom.Block
import com.wutsi.editorjs.dom.BlockData
import com.wutsi.editorjs.dom.BlockType
import com.wutsi.editorjs.dom.File
import org.jsoup.nodes.Element
import java.io.StringWriter

class Image : Tag {
    override fun write(block: Block, writer: StringWriter) {
        writer.write("<figure>")
        writeImg(block, writer)
        writeCaption(block, writer)
        writer.write("</figure>\n")
    }

    override fun read(elt: Element): Block? {
        val bloc = if ("figure" == elt.tagName().lowercase()) readFigure(elt) else readImage(elt)
        return if (bloc.data.file.url.isNullOrEmpty() && bloc.data.url.isNullOrEmpty()) {
            null
        } else {
            bloc
        }
    }

    private fun readFigure(elt: Element): Block {
        val img = elt.allElements.find { it.tagName().lowercase() == "img" }
        val caption = elt.allElements.find { it.tagName().lowercase() == "figcaption" }
        if (img != null) {
            val block = readImage(img)
            block.data.caption = if (caption == null) "" else caption.text()
            return block
        } else {
            return Block(
                type = BlockType.image,
                data = BlockData(
                    caption = if (caption == null) "" else caption.text(),
                ),
            )
        }
    }

    private fun readImage(elt: Element) = Block(
        type = BlockType.image,
        data = BlockData(
            url = elt.attr("src"),
            caption = elt.attr("alt"),
            withBorder = elt.hasClass("border"),
            withBackground = elt.hasClass("background"),
            stretched = elt.hasClass("stretched"),
            file = File(
                url = elt.attr("src"),
                width = intAttr(elt, "width"),
                height = intAttr(elt, "height"),
            ),
        ),
    )

    private fun intAttr(elt: Element, name: String): Int {
        try {
            return elt.attr(name).toInt()
        } catch (ex: Exception) {
            return -1
        }
    }

    private fun writeImg(block: Block, writer: StringWriter) {
        var url = block.data.file.url // ImageTool
        if (url.isEmpty()) {
            url = block.data.url // Fallback to SimpleImageTool
        }

        writer.write("<img src='$url'")

        writeAlt(block, writer)
        writeClass(block, writer)
        writeDimension(block, writer)

        writer.write(" />")
    }

    private fun writeClass(block: Block, writer: StringWriter) {
        var css = ""
        if (block.data.stretched) {
            css += " stretched"
        }
        if (block.data.withBorder) {
            css += " border"
        }
        if (block.data.withBackground) {
            css += " background"
        }
        css = css.trim()
        if (css.isNotBlank()) {
            writer.write(" class='$css'")
        }
    }

    private fun writeAlt(block: Block, writer: StringWriter) {
        val alt = block.data.caption
        if (alt.isNotBlank()) {
            writer.write(" alt='$alt'")
        }
    }

    private fun writeDimension(block: Block, wirter: StringWriter) {
        val width = block.data.file.width
        val height = block.data.file.height
        if (width > 0) {
            wirter.write(" width=$width")
        }
        if (height > 0) {
            wirter.write(" height=$height")
        }
    }

    private fun writeCaption(block: Block, writer: StringWriter) {
        val caption = block.data.caption
        if (caption.isNotBlank()) {
            writer.write("<figcaption>$caption</figcaption>")
        }
    }
}
