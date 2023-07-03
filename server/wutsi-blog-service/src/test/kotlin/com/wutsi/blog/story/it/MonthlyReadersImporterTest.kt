package com.wutsi.blog.story.it

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.blog.kpi.service.TrackingStorageService
import com.wutsi.blog.story.dao.ViewRepository
import com.wutsi.blog.story.domain.ViewEntity
import com.wutsi.blog.story.job.MonthlyReaderImporterJob
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import java.io.ByteArrayInputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class MonthlyReadersImporterTest {
    @Autowired
    private lateinit var job: MonthlyReaderImporterJob

    @Autowired
    private lateinit var storage: TrackingStorageService

    @MockBean
    private lateinit var dao: ViewRepository

    @Test
    fun run() {
        // GIVEN
        val now = LocalDate.now()
        storage.store(
            "kpi/monthly/" + now.format(DateTimeFormatter.ofPattern("yyyy/MM")) + "/readers.csv",
            ByteArrayInputStream(
                """
                    account_id,device_id,product_id, total_reads
                    1,device-1,100,10
                    2,device-2,200,20
                """.trimIndent().toByteArray(),
            ),
            "application/json",
        )
        storage.store(
            "kpi/monthly/" + now.minusMonths(1).format(DateTimeFormatter.ofPattern("yyyy/MM")) + "/readers.csv",
            ByteArrayInputStream(
                """
                    account_id,device_id,product_id, total_reads
                    "",device-n,100,99
                    2,device-2,300,21
                """.trimIndent().toByteArray(),
            ),
            "application/json",
        )

        // WHEN
        job.run()

        // THEN
        val view = argumentCaptor<ViewEntity>()
        verify(dao, times(4)).save(view.capture())

        verify(dao).save(ViewEntity(1L, "device-1", 100))
        verify(dao).save(ViewEntity(2L, "device-2", 200))
        verify(dao).save(ViewEntity(null, "device-n", 100))
        verify(dao).save(ViewEntity(2L, "device-2", 300))
    }
}
