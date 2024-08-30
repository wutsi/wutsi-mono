package com.wutsi.editorjs.html

import com.wutsi.editorjs.dom.Block
import com.wutsi.editorjs.dom.BlockData
import com.wutsi.editorjs.dom.BlockType
import com.wutsi.editorjs.dom.EJSDocument
import com.wutsi.editorjs.dom.File
import com.wutsi.editorjs.dom.Meta
import com.wutsi.editorjs.html.tag.TagProvider
import org.junit.jupiter.api.Test
import java.io.StringWriter
import kotlin.test.assertEquals

class EJSHtmlWriterTest {

    @Test
    fun write() {
        val doc = createDocument()
        val expected = """
            <h1>Editor.js</h1>
            <p>Hey. Meet the new Editor. On this page you can see it in action — try to edit this text</p>
            <ul><li>It is a block-styled editor</li><li>It returns clean data output in JSON</li><li>Designed to be extendable and pluggable with a simple API</li></ul>
            <hr />
            <figure><img src='/upload/temporary/o_488cfb382712d6af914301c73f376e8c.jpg' alt='Logo' class='stretched border background' /><figcaption>Logo</figcaption></figure>
            <pre class='code'>class Foo { }</pre>
            <a href='https://www.afrohustler.com/3-personalities-we-should-express-henceforward-this-2020/' title='3 Personalities We Should Express Henceforward This 2020' class='link-tool'><div class='link-tool'><div class='meta'><h2>3 Personalities We Should Express Henceforward This 2020</h2><p class='description'>As a businessperson and or employee, if you develop these 3 personalities, you will survive these trying times and come out stronger.</p><p class='site'>www.afrohustler.com</p></div><div class='image'><img src='https://www.afrohustler.com/wp-content/uploads/2020/05/3-Personalities-1110x398.jpg' alt='3 Personalities We Should Express Henceforward This 2020'/></div></div></a>
            <div class='button centered large'><a href='https://www.afrohustler.com/3-personalities-we-should-express-henceforward-this-2020/'>Read It</a></div>
        """.trimIndent()

        val sw = StringWriter()
        val writer = EJSHtmlWriter(TagProvider())
        writer.write(doc, sw)

        System.out.println(sw.toString())

        assertEquals(expected.trim(), sw.toString().trim())
    }

    private fun createDocument() = EJSDocument(
        blocks = arrayListOf(
            Block(
                type = BlockType.header,
                data = BlockData(
                    level = 1,
                    text = "Editor.js",
                ),
            ),
            Block(
                type = BlockType.paragraph,
                data = BlockData(
                    text = "Hey. Meet the new Editor. On this page you can see it in action — try to edit this text",
                ),
            ),
            Block(
                type = BlockType.list,
                data = BlockData(
                    items = arrayListOf(
                        "It is a block-styled editor",
                        "It returns clean data output in JSON",
                        "Designed to be extendable and pluggable with a simple API",
                    ),
                ),
            ),
            Block(
                type = BlockType.delimiter,
            ),
            Block(
                type = BlockType.image,
                data = BlockData(
                    caption = "Logo",
                    withBackground = true,
                    withBorder = true,
                    stretched = true,
                    file = File(
                        url = "/upload/temporary/o_488cfb382712d6af914301c73f376e8c.jpg",
                    ),
                ),
            ),
            Block(
                type = BlockType.code,
                data = BlockData(
                    code = "class Foo { }",
                ),
            ),
            Block(
                type = BlockType.linkTool,
                data = BlockData(
                    link = "https://www.afrohustler.com/3-personalities-we-should-express-henceforward-this-2020/",
                    meta = Meta(
                        title = "3 Personalities We Should Express Henceforward This 2020",
                        site_name = "www.afrohustler.com",
                        description = "As a businessperson and or employee, if you develop these 3 personalities, you will survive these trying times and come out stronger.",
                        image = File(
                            url = "https://www.afrohustler.com/wp-content/uploads/2020/05/3-Personalities-1110x398.jpg",
                        ),

                        ),
                ),
            ),
            Block(
                type = BlockType.button,
                data = BlockData(
                    url = "https://www.afrohustler.com/3-personalities-we-should-express-henceforward-this-2020/",
                    label = "Read It",
                    centered = true,
                    stretched = false,
                    large = true,
                ),
            ),
        ),
    )
}
