package com.wutsi.editorjs.html.tag.embed

import com.wutsi.editorjs.dom.Block
import com.wutsi.editorjs.dom.BlockData
import com.wutsi.editorjs.dom.BlockType
import com.wutsi.editorjs.html.tag.Tag
import org.apache.commons.text.StringEscapeUtils
import org.jsoup.nodes.Element
import java.io.StringWriter

abstract class AbstractEmbedVideo : Tag {
    abstract fun getDisplayName(): String

    abstract fun service(): String

    abstract fun cssClass(): String

    abstract fun extractId(url: String): String

    abstract fun getImageUrl(id: String): String

    override fun write(block: Block, writer: StringWriter) {
        if (block.data.service != service()) {
            return
        }

        val width = block.data.width
        val height = block.data.height
        val source = block.data.source
        val caption = StringEscapeUtils.escapeHtml4(block.data.caption)
        val id = extractId(source)
        val css = cssClass()
        val service = service()
        writer.write(
            """
                <div class='$css' data-id='$id' data-source='$source' data-width='$width' data-height='$height' data-caption='$caption'>
                    <div id='$service-$id' class='player'>
                        <a href='$source' target='_new' title='Play on ${getDisplayName()}'>
                            <img src='${getImageUrl(id)}' style='width: 100%'/>
                            <div class='text-center'>Play on ${getDisplayName()}</div>
                        </a>
                    </div>
                </div>
            """.trimIndent(),
        )
    }

    override fun read(elt: Element): Block? {
        val clazz = elt.attr("class")
        val id = elt.attr("data-id")
        if (id.isEmpty() || clazz != cssClass()) {
            return null
        }

        return Block(
            type = BlockType.embed,
            data = BlockData(
                caption = elt.attr("data-caption"),
                width = elt.attr("data-width"),
                height = elt.attr("data-height"),
                source = elt.attr("data-source"),
                embed = "https://twitframe.com/show?url=" + elt.attr("data-source"),
                service = service(),
            ),
        )
    }
}
