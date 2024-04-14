package com.wutsi.blog.app.service.ejs.interceptor

import com.wutsi.blog.app.model.StoryModel
import com.wutsi.blog.app.service.Toggles
import com.wutsi.blog.app.service.ejs.EJSInterceptor
import com.wutsi.editorjs.dom.Block
import com.wutsi.editorjs.dom.BlockData
import com.wutsi.editorjs.dom.BlockType
import com.wutsi.editorjs.dom.EJSDocument
import com.wutsi.editorjs.html.tag.embed.EmbedAdvertising

class AdsEJSInterceptor(
    private val toggles: Toggles,
) : EJSInterceptor {
    /**
     * Add the ads at position 1/3
     */
    override fun filter(doc: EJSDocument, story: StoryModel) {
        if (!toggles.ads) {
            return
        }

        val index1 = doc.blocks.size * .3
        if (index1 > 3) {
            insertAt(index1, doc)
        }

        val index2 = doc.blocks.size * .6
        if (index2 > index1 + 3) {
            insertAt(index2, doc)
        }

        val index3 = doc.blocks.size * .9
        if (index3 > index2 + 3) {
            insertAt(index3, doc)
        }
    }

    private fun insertAt(index: Double, doc: EJSDocument) {
        doc.blocks.add(
            index.toInt(),
            Block(
                type = BlockType.embed,
                data = BlockData(
                    service = EmbedAdvertising.SERVICE
                )
            ),
        )
    }
}
