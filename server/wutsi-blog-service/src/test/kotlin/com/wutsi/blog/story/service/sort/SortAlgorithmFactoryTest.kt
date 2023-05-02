package com.wutsi.blog.story.service.sort

import com.wutsi.blog.client.story.SortAlgorithmType
import com.wutsi.blog.story.service.sort.algo.MostRecentSortAlgorithm
import com.wutsi.blog.story.service.sort.algo.MostViewedSortAlgorithm
import com.wutsi.blog.story.service.sort.algo.NoSortAlgorithm
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
class SortAlgorithmFactoryTest {
    @Mock
    private lateinit var mostRecent: MostRecentSortAlgorithm

    @Mock
    private lateinit var mostViewed: MostViewedSortAlgorithm

    @Mock
    private lateinit var recommended: MostViewedSortAlgorithm

    @Mock
    private lateinit var none: NoSortAlgorithm

    private lateinit var factory: SortAlgorithmFactory

    @BeforeEach
    fun setUp() {
        factory = SortAlgorithmFactory(
            mostRecent,
            mostViewed,
            recommended,
            none,
        )
    }

    @Test
    fun getMostViewed() {
        assertEquals(mostViewed, factory.get(SortAlgorithmType.most_viewed))
    }

    @Test
    fun getMostRecent() {
        assertEquals(mostRecent, factory.get(SortAlgorithmType.most_recent))
    }

    @Test
    fun getRecommended() {
        assertEquals(recommended, factory.get(SortAlgorithmType.recommended))
    }

    @Test
    fun getNull() {
        assertEquals(none, factory.get(SortAlgorithmType.no_sort))
    }
}
