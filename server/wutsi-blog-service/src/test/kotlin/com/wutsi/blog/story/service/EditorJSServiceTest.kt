package com.wutsi.blog.story.service

import com.wutsi.editorjs.dom.Block
import com.wutsi.editorjs.dom.BlockData
import com.wutsi.editorjs.dom.BlockType
import com.wutsi.editorjs.dom.EJSDocument
import com.wutsi.editorjs.dom.File
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import java.io.StringWriter
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class EditorJSServiceTest {
    @Autowired
    private lateinit var service: EditorJSService

    @Test
    fun toText() {
        val doc =
            createDocument("Hey. <b>Meet the new <i>Editor</i></b>. On this page you can see it in action — try to edit this text")
        assertEquals(
            "Hey. Meet the new Editor. On this page you can see it in action — try to edit this text",
            service.toText(doc),
        )
    }

    @Test
    fun wordCountEmpty() {
        val doc = EJSDocument()
        assertEquals(0, service.wordCount(doc))
    }

    @Test
    fun wordCountHtml() {
        val doc =
            createDocument("Hey. <b>Meet the new <i>Editor</i></b>. On this page you can see it in action - try to edit this ( text)")
        assertEquals(19, service.wordCount(doc))
    }

    @Test
    fun detectLanguageEN() {
        val doc =
            createDocument("Hey. <b>Meet the new <i>Editor</i></b>. On this page you can see it in action — try to edit this text")
        assertEquals("en", service.detectLanguage("This is a sample text", null, doc))
    }

    @Test
    fun detectLanguageFR() {
        val doc =
            createDocument("La France est un pays attachant avec de magnifiques monuments et une savoureuse gastronomie. C'est pourquoi parler français lors de ses voyages ou pour nouer des relations professionnelles demeure un vrai plus !")
        assertEquals("fr", service.detectLanguage("La france aux francais", null, doc))
    }

    @Test
    fun detectLanguageUnsupported() {
        val doc = createDocument("-")
        assertEquals("en", service.detectLanguage("?????", null, doc))
    }

    @Test
    fun extractSummary() {
        val doc = createDocument(
            text = "Hey. <b>Meet the new <i>Editor</i></b>.<br/> On this page you can see it in action - try to edit this text",
            text2 = "This looks weird",
        )
        assertEquals(
            "Hey. Meet the new Editor. On this page you can see it in action - try to edit this text",
            service.extractSummary(doc, 200),
        )
    }

    @Test
    fun extractSummaryChopped() {
        val doc = createDocument(
            text = "Hey. <b>Meet the new <i>Editor</i></b>.<br/> On this page you can see it in action - try to edit this text",
            text2 = "This looks weird",
        )
        assertEquals("Hey. Meet the new Editor. On t...", service.extractSummary(doc, 30))
    }

    @Test
    fun extractThumbnailUrl() {
        val doc = createDocument(text = "Yo", imageUrl = "http://ggg.com/1.png")
        assertEquals("http://ggg.com/1.png", service.extractThumbnailUrl(doc))
    }

    @Test
    fun extractThumbnailUrlNone() {
        val doc = EJSDocument()
        assertEquals("", service.extractThumbnailUrl(doc))
    }

    private fun createDocument(text: String, text2: String = "", imageUrl: String = "http://ggg.com/1.png") =
        EJSDocument(
            blocks = mutableListOf(
                Block(
                    type = BlockType.paragraph,
                    data = BlockData(
                        text = text,
                    ),
                ),
                Block(
                    type = BlockType.image,
                    data = BlockData(
                        file = File(
                            url = imageUrl,
                        ),
                    ),
                ),
                Block(
                    type = BlockType.paragraph,
                    data = BlockData(
                        text = text2,
                    ),
                ),
            ),
        )

    private fun generateText(seed: String, wordCount: Int): String {
        var i = 0
        var writer = StringWriter()
        while (i++ < wordCount) {
            writer.append("$seed ")
        }
        return writer.toString()
    }
}
