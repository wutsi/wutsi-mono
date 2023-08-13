package com.wutsi.ml.personalize.job

import com.nhaarman.mockitokotlin2.verify
import com.wutsi.ml.personalize.service.PersonalizeV1Service
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class PersonalizeV1ReloadJobTest {
    @Autowired
    private lateinit var job: PersonalizeV1ReloadJob

    @MockBean
    private lateinit var service: PersonalizeV1Service

    @Test
    fun run() {
        // WHEN
        job.run()

        // THEN
        verify(service).init()
    }
}
