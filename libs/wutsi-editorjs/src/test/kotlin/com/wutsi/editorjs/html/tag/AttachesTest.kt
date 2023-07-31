package com.wutsi.editorjs.html.tag

import com.wutsi.editorjs.dom.Block
import com.wutsi.editorjs.dom.BlockData
import com.wutsi.editorjs.dom.BlockType.attaches
import com.wutsi.editorjs.dom.File
import org.junit.jupiter.api.Test
import java.io.StringWriter
import kotlin.test.assertEquals

class AttachesTest {
    val tag = Attaches()

    @Test
    fun writeCode() {
        val block = createBlock("https://www.google.ca/img.png", "img.png", 14394309)
        val writer = StringWriter()

        tag.write(block, writer)

        assertEquals(
            """
                <a href='https://www.google.ca/img.png' title='img.png' class='attaches'>
                  <div class='attaches'>
                    <div class='ext'><span class='png'>PNG</span></div>
                    <div class='file'>
                      <div class='filename'>img.png</div>
                      <div class='filesize'>14.4 Mb</div>
                    </div>
                  </div>
                </a>
            """.trimIndent(),
            writer.toString(),
        )
    }

    private fun createBlock(link: String, name: String, size: Long) = Block(
        type = attaches,
        data = BlockData(
            file = File(
                url = link,
                name = name,
                size = size,
            ),
        ),
    )
}
