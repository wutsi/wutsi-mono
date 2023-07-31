package com.wutsi.editorjs.html.tag

import com.wutsi.editorjs.dom.BlockType
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TagProviderTest {
    private val provider = TagProvider()

    @Test
    fun all() {
        val tags = provider.all()
        assertEquals(13, tags.size)
    }

    @Test
    fun get() {
        assertTrue(provider.get(BlockType.code) is Code)
        assertTrue(provider.get(BlockType.raw) is Code)
        assertTrue(provider.get(BlockType.delimiter) is Delimiter)
        assertTrue(provider.get(BlockType.header) is Header)
        assertTrue(provider.get(BlockType.image) is Image)
        assertTrue(provider.get(BlockType.list) is List)
        assertTrue(provider.get(BlockType.paragraph) is Paragraph)
        assertTrue(provider.get(BlockType.quote) is Quote)
        assertTrue(provider.get(BlockType.linkTool) is Link)
        assertTrue(provider.get(BlockType.embed) is Embed)
        assertTrue(provider.get(BlockType.button) is Button)
        assertTrue(provider.get(BlockType.AnyButton) is Button)
        assertTrue(provider.get(BlockType.attaches) is Attaches)
    }
}
