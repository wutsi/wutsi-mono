package com.wutsi.blog.kpi.it

import com.wutsi.blog.kpi.dto.KpiType
import com.wutsi.blog.kpi.dto.SearchStoryKpiRequest
import com.wutsi.blog.kpi.dto.SearchStoryKpiResponse
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/kpi/SearchStoryKpiQuery.sql"])
internal class SearchStoryKpiQueryTest {
    @Autowired
    private lateinit var rest: TestRestTemplate

    @Test
    fun search() {
        val request = SearchStoryKpiRequest(
            storyIds = listOf(100),
            types = listOf(KpiType.READ),
        )
        val result = rest.postForEntity("/v1/kpis/queries/search-story", request, SearchStoryKpiResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val stories = result.body!!.kpis
        assertEquals(3, stories.size)

        assertEquals(100L, stories[0].storyId)
        assertEquals(KpiType.READ, stories[0].type)
        assertEquals(2020, stories[0].year)
        assertEquals(1, stories[0].month)
        assertEquals(11, stories[0].value)

        assertEquals(100L, stories[1].storyId)
        assertEquals(KpiType.READ, stories[1].type)
        assertEquals(2020, stories[1].year)
        assertEquals(2, stories[1].month)
        assertEquals(12, stories[1].value)

        assertEquals(100L, stories[2].storyId)
        assertEquals(KpiType.READ, stories[2].type)
        assertEquals(2021, stories[2].year)
        assertEquals(9, stories[2].month)
        assertEquals(19, stories[2].value)
    }
}
