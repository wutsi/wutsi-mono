package com.wutsi.blog.story.service.filter

import com.wutsi.blog.story.dao.PreferredCategoryRepository
import com.wutsi.blog.story.domain.PreferredCategoryEntity
import com.wutsi.blog.story.domain.StoryEntity
import com.wutsi.blog.story.dto.SearchStoryRequest
import com.wutsi.blog.story.dto.StorySortStrategy
import com.wutsi.blog.story.service.StorySearchFilter
import org.springframework.stereotype.Service

@Service
class PreferredCategoryStorySearchFilter(
    private val preferredCategoryDao: PreferredCategoryRepository,
) : StorySearchFilter {
    companion object {
        const val MAX_CATEGORIES = 5
    }

    override fun filter(request: SearchStoryRequest, stories: List<StoryEntity>): List<StoryEntity> {
        val userId = request.searchContext?.userId
        if (request.sortBy != StorySortStrategy.RECOMMENDED || userId == null) {
            return stories
        }

        val categories: List<PreferredCategoryEntity?> = preferredCategoryDao.findByUserIdOrderByTotalReadsDesc(userId)
        val categoryIds = categories
            .map { category -> category?.categoryId }
            .filterNotNull()
            .take(MAX_CATEGORIES)
        if (categoryIds.isEmpty()) {
            return stories
        }

        val result = mutableListOf<StoryEntity>()
        categoryIds.forEach { categoryId ->
            result.addAll(
                stories.filter { story -> story.categoryId == categoryId }
            )
        }
        result.addAll(
            stories.filter { story ->
                story.categoryId == null || !categoryIds.contains(story.categoryId)
            }
        )
        return result
    }
}
