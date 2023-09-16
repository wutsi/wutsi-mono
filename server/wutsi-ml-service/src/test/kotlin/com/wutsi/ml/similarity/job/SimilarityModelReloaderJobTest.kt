package com.wutsi.ml.similarity.job

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.ml.similarity.dto.SimilarityModelType
import com.wutsi.ml.similarity.model.SimilarityModel
import com.wutsi.ml.similarity.model.SimilarityModelFactory
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SimilarityModelReloaderJobTest {
    @Autowired
    private lateinit var job: SimilarityModelReloaderJob

    @MockBean
    private lateinit var factory: SimilarityModelFactory

    @Test
    fun run() {
        // GIVEN
        val authorModel = mock<SimilarityModel>()
        val storyModel = mock<SimilarityModel>()
        doReturn(authorModel).whenever(factory).get(SimilarityModelType.AUTHOR_TIFDF)
        doReturn(storyModel).whenever(factory).get(SimilarityModelType.STORY_TIFDF)

        // WHEN
        job.run()

        // THEN
        verify(authorModel).reload()
        verify(storyModel).reload()
    }
}
