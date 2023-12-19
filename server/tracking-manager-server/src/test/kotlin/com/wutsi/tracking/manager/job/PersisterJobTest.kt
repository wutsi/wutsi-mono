package com.wutsi.tracking.manager.job

import com.nhaarman.mockitokotlin2.verify
import com.wutsi.tracking.manager.service.pipeline.filter.PersisterFilter
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class PersisterJobTest {
    @MockBean
    private lateinit var filter: PersisterFilter

    @Autowired
    private lateinit var job: PersisterJob

    @Test
    fun run() {
        job.run()

        verify(filter).flush()
    }
}
