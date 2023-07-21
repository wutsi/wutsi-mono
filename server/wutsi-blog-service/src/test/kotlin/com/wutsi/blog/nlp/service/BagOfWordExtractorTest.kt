package com.wutsi.blog.nlp.service

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class BagOfWordExtractorTest {
    private val extractor = BagOfWordExtractor()

    @Test
    fun extractEn() {
        val sw = StopWords(listOf("the", "is", "a", "an", "is", "not", "in"))
        val text =
            "The dog is a pet. An orange is not a pet in 2023"
        val bow = extractor.extract(text, sw)

        bow.forEach { println(it) }
        assertEquals(3, bow.size)
        assertEquals(Term("pet", 0.5), bow[0])
        assertEquals(Term("dog", 0.25), bow[1])
        assertEquals(Term("orange", 0.25), bow[2])
    }

    @Test
    fun extractFr() {
        val sw = StopWords(listOf("je", "suis", "a", "des", "is", "not"))
        val text =
            "Je suis occupé a manger des pattes. Des \"pattes\"?"
        val bow = extractor.extract(text, sw)

        bow.forEach { println(it) }
        assertEquals(3, bow.size)
        assertEquals(Term("pattes", 0.5), bow[0])
        assertEquals(Term("occupé", 0.25), bow[1])
        assertEquals(Term("manger", 0.25), bow[2])
    }
}
