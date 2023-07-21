package com.wutsi.editorjs.html.tag

import com.wutsi.editorjs.dom.Block
import com.wutsi.editorjs.dom.BlockData
import com.wutsi.editorjs.dom.BlockType
import com.wutsi.editorjs.dom.File
import com.wutsi.editorjs.dom.Meta
import org.apache.commons.text.StringEscapeUtils
import org.jsoup.nodes.Element
import org.slf4j.LoggerFactory
import java.io.StringWriter

class Link : Tag {
    companion object {
        const val CLASSNAME = "link-tool"
        private val LOGGER = LoggerFactory.getLogger(Link::class.java)
    }

    override fun write(block: Block, writer: StringWriter) {
        val data = block.data
        val title = StringEscapeUtils.escapeHtml4(data.meta.title)
        val description = StringEscapeUtils.escapeHtml4(data.meta.description)
        val siteName = StringEscapeUtils.escapeHtml4(data.meta.site_name)
        val image = data.meta.image.url

        writer.write("<a href='${data.link}' title='$title' class='$CLASSNAME'>")
        writer.write("<div class='$CLASSNAME'>")
        writer.write("<div class='meta'>")
        writer.write("<h2>$title</h2>")
        writer.write("<p class='description'>$description</p>")
        writer.write("<p class='site'>$siteName</p>")
        writer.write("</div>")

        if (image.isNotEmpty()) {
            writer.write("<div class='image'>")
            writer.write("<img src='$image' alt='$title'/>")
            writer.write("</div>")
        }
        writer.write("</div>")
        writer.write("</a>\n")
    }

    override fun read(elt: Element): Block? {
        if (!elt.hasClass(CLASSNAME)) {
            return null
        }

        val url = elt.attr("abs:href")
        val title = elt.select(".meta h2").text()
        val description = elt.select(".meta p.description").text()
        val siteName = elt.select(".meta p.site").text()
        val image = elt.select(".image img").attr("src")
        return Block(
            type = BlockType.linkTool,
            data = BlockData(
                link = url,
                meta = Meta(
                    title = title,
                    description = description,
                    site_name = siteName,
                    image = File(
                        url = image,
                    ),
                ),
            ),
        )
    }
}
