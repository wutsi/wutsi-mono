package com.wutsi.editorjs.utils

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TextUtilsTest {
    val text = "Hey! Meet the new Editor. On this page you can see it in action - try to edit this ( text)?... No"

    @Test
    fun sentences() {
        val sentences = TextUtils.sentences(text)
        assertEquals(4, sentences.size)
        assertEquals("Hey", sentences[0])
        assertEquals("Meet the new Editor", sentences[1])
        assertEquals("On this page you can see it in action - try to edit this ( text)", sentences[2])
        assertEquals("No", sentences[3])
    }

    @Test
    fun words() {
        val words = TextUtils.words(text)
        assertEquals(20, words.size)

    }

}
