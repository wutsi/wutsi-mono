package com.wutsi.editorjs.dom

data class BlockData(
    var text: String = "",

    var level: Int = 1,

    var items: List<Any> = emptyList(), // The schemas was changed from List<String> to List<ListItem>
    var style: ListStyle = ListStyle.unordered,

    var url: String = "",
    var caption: String = "",
    var withBorder: Boolean = false,
    var stretched: Boolean = false,
    var withBackground: Boolean = false,

    var code: String = "",
    var alignment: String = "",

    var file: File = File(),

    var link: String = "",
    var meta: Meta = Meta(),

    var embed: String = "",
    var service: String = "",
    var width: String = "",
    var height: String = "",
    var source: String = "",

    var html: String = "",

    var label: String = "",
    var centered: Boolean = false,
    var large: Boolean = false,
)
