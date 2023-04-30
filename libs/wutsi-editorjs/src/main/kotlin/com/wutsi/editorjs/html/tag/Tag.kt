package com.wutsi.editorjs.html.tag

import com.wutsi.editorjs.dom.Block
import org.jsoup.nodes.Element
import java.io.StringWriter

interface Tag {
    fun write (block: Block, writer: StringWriter)

    fun read(elt: Element): Block?
}
