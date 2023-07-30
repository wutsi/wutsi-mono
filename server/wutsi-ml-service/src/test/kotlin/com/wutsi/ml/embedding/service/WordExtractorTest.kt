package com.wutsi.ml.embedding.service

import com.wutsi.ml.document.domain.DocumentEntity
import com.wutsi.ml.document.domain.WordEntity
import com.wutsi.ml.document.service.WordExtractor
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class WordExtractorTest {
    @Autowired
    private lateinit var extractor: WordExtractor

    @Test
    fun extractEn() {
        val doc = DocumentEntity(
            id = 11,
            content = "The dog is a pet. An orange is not a pet in 2023",
            language = "en",
        )
        val bow = extractor.extract(doc)

        bow.forEach { println(it) }
        assertEquals(3, bow.size)
        assertEquals(WordEntity("dog", 0.25), bow[0])
        assertEquals(WordEntity("pet", 0.5), bow[1])
        assertEquals(WordEntity("orange", 0.25), bow[2])
    }

    @Test
    fun extractFr() {
        val doc = DocumentEntity(
            id = 11,
            content = "Je suis occupé a manger des pattes. Des \"pattes\"?",
            language = "fr",
        )
        val bow = extractor.extract(doc)

        bow.forEach { println(it) }
        assertEquals(3, bow.size)
        assertEquals(WordEntity("occupé", 0.25), bow[0])
        assertEquals(WordEntity("manger", 0.25), bow[1])
        assertEquals(WordEntity("pattes", 0.5), bow[2])
    }
}
