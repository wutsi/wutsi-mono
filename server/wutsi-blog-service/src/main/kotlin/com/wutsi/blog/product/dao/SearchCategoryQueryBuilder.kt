package com.wutsi.blog.product.dao

import com.wutsi.blog.product.dto.SearchCategoryRequest
import com.wutsi.blog.util.Predicates

class SearchCategoryQueryBuilder {
    fun query(request: SearchCategoryRequest): String {
        val select = select()
        val from = from()
        val where = where(request)
        val limit = limit(request)
        val offset = offset(request)

        return "$select $from $where $limit $offset"
    }

    fun parameters(request: SearchCategoryRequest): Array<Any> {
        return Predicates.parameters(
            request.categoryIds,
            request.level,
            request.parentId,
            request.keyword?.let { "${request.keyword}%" },
        )
    }

    private fun select() = "SELECT *"

    private fun from() = "FROM T_CATEGORY"

    private fun where(request: SearchCategoryRequest): String {
        val predicates = mutableListOf<String?>()
        predicates.add(Predicates.`in`("id", request.categoryIds))
        predicates.add(Predicates.eq("level", request.level))
        predicates.add(Predicates.eq("parent_fk", request.parentId))
        if (!request.keyword.isNullOrEmpty()) {
            if (request.language?.equals("fr", true) == true) {
                predicates.add(Predicates.like("LCASE(title_french_ascii)", request.keyword))
            } else {
                predicates.add(Predicates.like("LCASE(title)", request.keyword))
            }
        }

        return Predicates.where(predicates)
    }

    private fun limit(request: SearchCategoryRequest) = "LIMIT ${request.limit}"

    private fun offset(request: SearchCategoryRequest) = "OFFSET ${request.offset}"
}
