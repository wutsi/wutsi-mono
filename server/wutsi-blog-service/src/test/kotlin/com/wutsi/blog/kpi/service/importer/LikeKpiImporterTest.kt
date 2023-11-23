package com.wutsi.blog.kpi.service.importer

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.kpi.service.KpiPersister
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import java.time.LocalDate
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LikeKpiImporterTest {
    @MockBean
    private lateinit var persister: KpiPersister

    @Autowired
    private lateinit var importer: LikeKpiImporter

    @Test
    fun import() {
        val date = LocalDate.now()
        doReturn(5).whenever(persister).persistUserLike(any())
        doReturn(3).whenever(persister).persistStoryLike(any())

        val result = importer.import(date)

        assertEquals(8, result)
        verify(persister).persistStoryLike(date)
        verify(persister).persistUserLike(date)
    }
}
