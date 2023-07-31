package com.wutsi.editorjs.html.tag

import com.wutsi.editorjs.dom.Block
import org.apache.commons.text.StringEscapeUtils
import org.jsoup.nodes.Element
import java.io.StringWriter
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.StringCharacterIterator

class Attachment : Tag {
    companion object {
        const val CLASSNAME = "attachment"
    }

    override fun write(block: Block, writer: StringWriter) {
        val data = block.data
        val filename = StringEscapeUtils.escapeHtml4(data.file.name)
        val size = toHumanReadable(data.file.size)

        writer.write(
            """
                <a href='${data.file.url}' title='$filename' class='$CLASSNAME'>
                  <div class='$CLASSNAME'>
                    <div class='ext'>${data.file.extension.uppercase()}</div>
                    <div>
                      <div class='filename'>$filename</b>
                      <div class='size'>$size</p>
                    </div>
                  </div>
                </a>
            """.trimIndent()
        )
    }

    override fun read(elt: Element): Block? =
        null

    private fun toHumanReadable(
        value: Long,
        fmt: NumberFormat = DecimalFormat("#.#"),
    ): String {
        var bytes = value
        if (bytes == 0L) {
            return "0"
        } else if (-1000 < bytes && bytes < 1000) {
            return bytes.toString()
        }
        val ci = StringCharacterIterator("KMGTPE")
        while (bytes <= -999950 || bytes >= 999950) {
            bytes /= 1000
            ci.next()
        }

        return fmt.format(bytes / 1000.0) + " " + ci.current()
    }
}
