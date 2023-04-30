package com.wutsi.editorjs.json

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.editorjs.dom.EJSDocument

class EJSJsonReader(private val mapper: ObjectMapper) {
    fun read(json: String) = mapper.readValue(json, EJSDocument::class.java)

    fun read(json: String, summary: Boolean): EJSDocument {
        val doc = read(json)
        if (summary) {
            val max = Math.min(4, doc.blocks.size / 3)
            return EJSDocument(
                time = doc.time,
                version = doc.version,
                blocks = doc.blocks.subList(0, max)
            )
        }
        return doc
    }
}
