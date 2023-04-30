package com.wutsi.editorjs.dom

data class Block (
        var type: BlockType = BlockType.paragraph,
        var data: BlockData = BlockData()
)
