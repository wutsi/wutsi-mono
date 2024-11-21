package com.wutsi.editorjs.dom

data class ListItem(
    val content: String = "",
    val items: List<Any> = emptyList(),
    val meta: Map<String, Any> = emptyMap(),
)
