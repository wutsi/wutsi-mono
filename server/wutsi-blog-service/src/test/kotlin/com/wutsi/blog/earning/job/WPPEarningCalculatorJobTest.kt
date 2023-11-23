package com.wutsi.blog.earning.job

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import org.apache.commons.io.IOUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.jdbc.Sql
import java.io.File
import java.io.FileInputStream
import java.text.SimpleDateFormat
import java.time.Clock

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/earning/WPPEarningCalculatorJob.sql"])
class WPPEarningCalculatorJobTest {
    @Autowired
    private lateinit var job: WPPEarningCalculatorJob

    @MockBean
    private lateinit var clock: Clock

    @Value("\${wutsi.platform.storage.local.directory}")
    private lateinit var storageDir: String

    @BeforeEach
    fun setUp() {
        File(storageDir).deleteRecursively()

        val date = SimpleDateFormat("yyyy-MM-dd").parse("2020-02-01")
        doReturn(date.time).whenever(clock).millis()
    }

    @Test
    fun run() {
        job.run()

        assertFile(
            File("$storageDir/earnings/2020/01/wpp-story.csv"),
            """
                story_id,user_id,read_count,reader_count,like_count,comment_count,click_count,read_time,earning_ratio,earning_adjustment,engagement_ratio,earnings,bonus,total
                100,111,100,100,15,5,10,400,0.37209302325581395,1.0,0.22900763358778625,74410,6420,80830
                200,211,475,475,60,30,0,300,0.27906976744186046,0.9,0.6870229007633588,50230,19270,69500
                201,211,50,40,5,1,1,100,0.09302325581395349,0.4,0.05343511450381679,7440,1490,8930
                202,211,75,60,2,0,2,75,0.06976744186046512,0.8,0.030534351145038167,11160,850,12010
                300,311,350,300,0,0,0,200,0.18604651162790697,0.7714285714285714,0.0,28700,0,28700
            """.trimIndent(),
        )

        assertFile(
            File("$storageDir/earnings/2020/01/wpp-user.csv"),
            """
                user_id,earnings,bonus,total
                111,74410,6420,80830
                211,68830,21610,90440
                311,28700,0,28700
            """.trimIndent(),
        )
    }

    private fun assertFile(file: File, content: String) {
        assertTrue(file.exists())
        assertEquals(content, IOUtils.toString(FileInputStream(file), Charsets.UTF_8).trimIndent())
    }
}
