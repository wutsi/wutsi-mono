package com.wutsi.blog.story.service.sort

import com.wutsi.blog.client.story.SortAlgorithmType
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

@Service
class SortAlgorithmFactory(
    @Qualifier("MostRecentSortAlgorithm") private val mostRecent: SortAlgorithm,
    @Qualifier("MostViewedSortAlgorithm") private val mostViewed: SortAlgorithm,
    @Qualifier("RecommendedSortAlgorithm") private val recommended: SortAlgorithm,
    @Qualifier("NoSortAlgorithm") private val none: SortAlgorithm,
) {
    fun get(algo: SortAlgorithmType): SortAlgorithm {
        if (algo == SortAlgorithmType.most_viewed) {
            return mostViewed
        } else if (algo == SortAlgorithmType.most_recent) {
            return mostRecent
        } else if (algo == SortAlgorithmType.recommended) {
            return recommended
        } else {
            return none
        }
    }
}
