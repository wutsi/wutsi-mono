package com.wutsi.editorjs.html

import com.wutsi.editorjs.dom.EJSDocument
import com.wutsi.editorjs.html.tag.TagProvider
import java.io.StringWriter

class EJSHtmlWriter(private val tags: TagProvider) {
    fun write (doc: EJSDocument, writer: StringWriter) {
        doc.blocks.forEach {
            val tag = tags.get(it.type)
            tag?.write(it, writer)
        }
    }
}
