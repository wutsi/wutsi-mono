package com.wutsi.blog.story.it

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.blog.kpi.service.TrackingStorageService
import com.wutsi.blog.story.dao.ReaderRepository
import com.wutsi.blog.story.dao.StoryRepository
import com.wutsi.blog.story.dao.ViewRepository
import com.wutsi.blog.story.domain.ViewEntity
import com.wutsi.blog.story.job.MonthlyReaderImporterJob
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.jdbc.Sql
import java.io.ByteArrayInputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/story/MonthlyReadersImporterJob.sql"])
internal class MonthlyReadersImporterTest {
    @Autowired
    private lateinit var job: MonthlyReaderImporterJob

    @Autowired
    private lateinit var storage: TrackingStorageService

    @MockBean
    private lateinit var viewDao: ViewRepository

    @Autowired
    private lateinit var readerDao: ReaderRepository

    @Autowired
    private lateinit var storyDao: StoryRepository

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
                    3,device-3,100,33
                    2,device-2,110,20
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
                    2,device-2,100,1
                    4,device-4,100,1
                    2,device-2,120,21
                    1,device-1,200,21
                """.trimIndent().toByteArray(),
            ),
            "application/json",
        )

        // WHEN
        job.run()

        // THEN
        val view = argumentCaptor<ViewEntity>()
        verify(viewDao, times(8)).save(view.capture())

        verify(viewDao).save(ViewEntity(1L, "device-1", 100))
        verify(viewDao).save(ViewEntity(2L, "device-2", 100))
        verify(viewDao).save(ViewEntity(3L, "device-3", 100))
        verify(viewDao).save(ViewEntity(4L, "device-4", 100))
        verify(viewDao).save(ViewEntity(null, "device-n", 100))

        verify(viewDao).save(ViewEntity(2L, "device-2", 110))

        verify(viewDao).save(ViewEntity(2L, "device-2", 120))

        verify(viewDao).save(ViewEntity(1L, "device-1", 200))

        assertTrue(readerDao.findByUserIdAndStoryId(1L, 100L).isPresent)
        assertTrue(readerDao.findByUserIdAndStoryId(2L, 100L).isPresent)
        assertTrue(readerDao.findByUserIdAndStoryId(3L, 100L).isPresent)
        assertTrue(readerDao.findByUserIdAndStoryId(2L, 110L).isPresent)
        assertTrue(readerDao.findByUserIdAndStoryId(2L, 120L).isPresent)
        assertTrue(readerDao.findByUserIdAndStoryId(1L, 200L).isPresent)

        assertEquals(2, storyDao.findById(100L).get().subscriberReaderCount)
        assertEquals(1, storyDao.findById(110L).get().subscriberReaderCount)
        assertEquals(1, storyDao.findById(120L).get().subscriberReaderCount)
        assertEquals(0, storyDao.findById(200L).get().subscriberReaderCount)
    }
}
