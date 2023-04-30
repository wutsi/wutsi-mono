package com.wutsi.editorjs.dom

data class EJSDocument(
    var time: Long = -1,
    var version: String = "2.8.1",
    var blocks: MutableList<Block> = mutableListOf(),
)
