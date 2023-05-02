package com.wutsi.blog.story.service.sort.algo

import com.wutsi.blog.client.story.SortStoryRequest
import com.wutsi.blog.story.service.ViewService
import com.wutsi.blog.story.service.sort.SortAlgorithm
import org.springframework.stereotype.Service

@Service("MostViewedSortAlgorithm")
class MostViewedSortAlgorithm(
    private val viewService: ViewService,
) : SortAlgorithm {
    override fun sort(request: SortStoryRequest): List<Long> {
        val values = request.storyIds
            .map { it to (viewService.get(it)?.size ?: 0) }
            .toMap()

        val notPopular = request.storyIds.filter { values[it] == null }

        val comparator = MostViewedComparator(values)
        val popular = request.storyIds
            .filter { values[it] != null }
            .sortedWith(comparator)

        val result = mutableListOf<Long>()
        result.addAll(popular)
        result.addAll(notPopular)
        return result
    }
}

class MostViewedComparator(private val values: Map<Long, Int>) : Comparator<Long> {
    override fun compare(id1: Long, id2: Long): Int {
        val value1 = values[id1]!!
        val value2 = values[id2]!!
        return value2.compareTo(value1)
    }
}
