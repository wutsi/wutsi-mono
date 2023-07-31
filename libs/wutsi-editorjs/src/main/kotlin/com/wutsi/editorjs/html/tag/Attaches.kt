package com.wutsi.editorjs.html.tag

import com.wutsi.editorjs.dom.Block
import org.apache.commons.text.StringEscapeUtils
import org.jsoup.nodes.Element
import java.io.StringWriter
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.StringCharacterIterator

class Attaches : Tag {
    companion object {
        const val CLASSNAME = "attaches"
    }

    override fun write(block: Block, writer: StringWriter) {
        val data = block.data
        val filename = StringEscapeUtils.escapeHtml4(data.file.name)
        val i = filename.lastIndexOf(".")
        val extension = if (i > 0) filename.substring(i + 1) else ""
        val size = toHumanReadable(data.file.size)

        writer.write(
            """
                <a href='${data.file.url}' title='$filename' class='$CLASSNAME'>
                  <div class='$CLASSNAME'>
                    <div class='ext'><span class='${extension.lowercase()}'>${extension.uppercase()}</span></div>
                    <div class='file'>
                      <div class='filename'>$filename</div>
                      <div class='filesize'>$size</div>
                    </div>
                  </div>
                </a>
            """.trimIndent(),
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
            return "0 b"
        } else if (bytes < 1000) {
            return "1 Kb"
        }
        val ci = StringCharacterIterator("KMGTPE")
        while (bytes <= -999950 || bytes >= 999950) {
            bytes /= 1000
            ci.next()
        }

        return fmt.format(bytes / 1000.0) + " " + ci.current() + "b"
    }
}
