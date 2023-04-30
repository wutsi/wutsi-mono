package com.wutsi.editorjs.html.tag

import com.wutsi.editorjs.dom.Block
import com.wutsi.editorjs.dom.BlockData
import com.wutsi.editorjs.dom.BlockType
import org.apache.commons.text.StringEscapeUtils
import org.jsoup.nodes.Element
import java.io.StringWriter

class Code: Tag {
    override fun write (block: Block, writer: StringWriter) {
        if (block.type == BlockType.code){
            val code = StringEscapeUtils.escapeHtml4(block.data.code)
            writer.write("<pre class='code'>$code</pre>\n")
        } else if (block.type == BlockType.raw) {
            val html = StringEscapeUtils.escapeHtml4(block.data.html)
            writer.write("<pre class='raw'>$html</pre>\n")
        }
    }

    override fun read(elt: Element): Block {
        return if (elt.hasClass("code")){
            Block(
                    type = BlockType.code,
                    data = BlockData(
                            code = elt.html()
                    )
            )
        } else {
            Block(
                    type = BlockType.raw,
                    data = BlockData(
                            html = elt.html()
                    )
            )
        }
    }

}
