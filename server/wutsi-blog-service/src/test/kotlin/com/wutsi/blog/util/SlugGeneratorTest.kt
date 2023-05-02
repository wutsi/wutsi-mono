package com.wutsi.blog.util

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class SlugGeneratorTest {
    @Test
    fun generate() {
        assertEquals("/read/123/this-is-a-slug", SlugGenerator.generate("/read/123", "This is a Slug"))
    }

    @Test
    fun filterDash() {
        assertEquals("/read/123/this-is-a-slug", SlugGenerator.generate("/read/123", "This-is a Slug"))
    }

    @Test
    fun filterMultipleDash() {
        assertEquals("/read/123/this-is-a-slug", SlugGenerator.generate("/read/123", "This-is a ,Slug"))
    }

    @Test
    fun filterPuctuation() {
        assertEquals("/read/123/this-is-a-slug", SlugGenerator.generate("/read/123", "This.is!a,Slug"))
    }

    @Test
    fun filterTrailingSeparator() {
        assertEquals("/read/123/this-is-a-slug", SlugGenerator.generate("/read/123", "This is a Slug?"))
    }
}
