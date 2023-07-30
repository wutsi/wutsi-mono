package com.wutsi.ml.embedding.service

import com.wutsi.ml.document.domain.DocumentEntity
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.io.ByteArrayOutputStream

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class TfIdfEmbeddingGeneratorTest {
    @Autowired
    private lateinit var generator: TfIdfEmbeddingGenerator

    val doc1 = DocumentEntity(
        id = 1L,
        content = "Sport Football. Roger Milla retoune a la Fecafoot. Roger va essayer de remettre la Fecafoot, les Lions Imdomptable et le football camerounais sur les rails",
        language = "fr",
    )

    val doc2 = DocumentEntity(
        id = 2L,
        content = "Sport Football. Samuel Eto'o mobilise les lions indomptables et la FECAFOOT. A la veille de la Coupe du Monde en France, les Lions se pret!",
        language = "fr",
    )
    val doc3 = DocumentEntity(
        id = 3L,
        content = "Politique International. Accueil majestueux pou Vladimir Putim a Paris.",
        language = "fr",
    )

    @Test
    fun generate() {
        val out = ByteArrayOutputStream()
        generator.generate(listOf(doc1, doc2, doc3), out)

        println(out.toString())
    }
}
