package com.wutsi.editorjs.html.tag

import com.wutsi.editorjs.dom.Block
import org.apache.commons.text.StringEscapeUtils
import org.jsoup.nodes.Element
import java.io.StringWriter
import java.lang.StringBuilder

class Button : Tag {
    override fun write(block: Block, writer: StringWriter) {
        val link = link(block)
        val text = StringEscapeUtils.escapeHtml4(text(block))
        val clazz = clazz(block)
        writer.write("<div class='$clazz'><a href='$link'>$text</a></div>\n")
    }

    override fun read(elt: Element): Block? = null

    private fun text(block: Block): String =
        if (block.data.label.isNullOrEmpty()) {
            block.data.text
        } else
            block.data.label // This will allow supporting AnyButton

    private fun link(block: Block): String =
        if (block.data.url.isNullOrEmpty()) {
            block.data.link
        } else
            block.data.url // This will allow supporting AnyButton

    private fun clazz(block: Block): String {
        val buff = StringBuilder()
        buff.append("button")
        if (block.data.stretched) {
            buff.append(" stretched")
        }
        if (block.data.centered) {
            buff.append(" centered")
        }
        if (block.data.large) {
            buff.append(" large")
        }
        return buff.toString()
    }
}
