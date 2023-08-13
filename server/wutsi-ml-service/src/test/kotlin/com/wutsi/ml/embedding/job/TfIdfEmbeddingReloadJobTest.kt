package com.wutsi.ml.embedding.job

import com.nhaarman.mockitokotlin2.verify
import com.wutsi.ml.embedding.service.TfIdfEmbeddingService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class TfIdfEmbeddingReloadJobTest {
    @Autowired
    private lateinit var job: TfIdfEmbeddingReloadJob

    @MockBean
    private lateinit var service: TfIdfEmbeddingService

    @Test
    fun run() {
        // WHEN
        job.run()

        // THEN
        verify(service).init()
    }
}
