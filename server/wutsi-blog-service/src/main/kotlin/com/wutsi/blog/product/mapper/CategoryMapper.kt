package com.wutsi.blog.product.mapper

import com.wutsi.blog.product.domain.CategoryEntity
import com.wutsi.blog.product.dto.Category
import org.springframework.stereotype.Service

@Service
class CategoryMapper {
    fun toCategory(category: CategoryEntity) = Category(
        id = category.id,
        title = category.title,
        titleFrench = category.titleFrench,
        titleFrenchAscii = category.titleFrenchAscii,
        level = category.level,
        parentId = category.parent?.id,
        longTitle = category.longTitle,
        longTitleFrench = category.longTitleFrench
    )
}
