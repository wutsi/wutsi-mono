package com.wutsi.blog.product.service.filter

import com.wutsi.blog.product.domain.ProductEntity
import com.wutsi.blog.product.dto.SearchProductRequest
import com.wutsi.blog.product.service.ProductSearchFilter
import com.wutsi.blog.story.dao.StoryRepository
import com.wutsi.blog.util.StringUtils
import org.apache.commons.text.similarity.LevenshteinDistance
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull

/**
 * Bubble up products having title similar to story
 */
@Service
class TaggedProductSearchFilter(
    private val storyDao: StoryRepository,
) : ProductSearchFilter {
    override fun filter(request: SearchProductRequest, products: List<ProductEntity>): List<ProductEntity> {
        val storyId = request.searchContext?.storyId
        if (storyId == null || products.isEmpty()) {
            return products
        }

        // Story
        val story = storyDao.findById(storyId).getOrNull() ?: return products
        val storyTitle = StringUtils.generate("", story.title ?: "")

        // Product titles
        val productTitles = products.associate { it.id to StringUtils.generate("", it.title) }

        // Sort product vs story distance
        val algo = LevenshteinDistance.getDefaultInstance()
        val sorted = products.map { it }.sortedWith { a, b ->
            algo.apply(storyTitle, productTitles[a.id]) - algo.apply(storyTitle, productTitles[b.id])
        }

        // Pick the 1st product as tagged product
        val tagged = sorted.first()

        // Result
        val result = mutableListOf<ProductEntity>()
        result.add(tagged)
        result.addAll(products.filter { it.id != tagged.id })
        return result
    }
}
