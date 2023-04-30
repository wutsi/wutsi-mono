package com.wutsi.editorjs.json

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.editorjs.dom.EJSDocument
import java.io.StringWriter

class EJSJsonWriter(private val mapper: ObjectMapper) {
    fun write(doc: EJSDocument, writer: StringWriter) {
        mapper.writerWithDefaultPrettyPrinter().writeValue(writer, doc)
    }
}
