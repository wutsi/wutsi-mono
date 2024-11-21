package com.wutsi.editorjs.json

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.editorjs.dom.BlockType
import com.wutsi.editorjs.dom.EJSDocument
import com.wutsi.editorjs.dom.ListItem

class EJSJsonReader(private val mapper: ObjectMapper) {
    fun read(json: String): EJSDocument {
        val doc = mapper.readValue(json, EJSDocument::class.java)
        return convertListItems(doc)
    }

    fun read(json: String, summary: Boolean): EJSDocument {
        val doc = read(json)
        if (summary) {
            val max = Math.min(4, doc.blocks.size / 3)
            return EJSDocument(
                time = doc.time,
                version = doc.version,
                blocks = doc.blocks.subList(0, max),
            )
        }
        return doc
    }

    private fun convertListItems(doc: EJSDocument): EJSDocument {
        val blocs = doc.blocks.filter { bloc -> bloc.type == BlockType.list }
        if (blocs.isNotEmpty()) {
            blocs.forEach { bloc ->
                bloc.data.items = bloc.data.items.map { item ->
                    if (item is Map<*, *>) {
                        toListItem(item)
                    } else {
                        ListItem(content = item.toString())
                    }
                }
            }
        }
        return doc
    }

    private fun toListItem(item: Map<*, *>): ListItem {
        return ListItem(
            content = item["content"]?.toString() ?: "",
            meta = (item["meta"] ?: emptyMap<String, Any>()) as Map<String, Any>,
            items = ((item["items"] ?: emptyList<Any>()) as List<Any>).map { item ->
                if (item is Map<*, *>) {
                    toListItem(item)
                } else {
                    ListItem(content = item.toString())
                }
            },
        )
    }
}
