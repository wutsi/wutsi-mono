package com.wutsi.blog.app.mapper

import com.wutsi.blog.app.model.CategoryModel
import com.wutsi.blog.product.dto.Category
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Service

@Service
class CategoryMapper {
    fun toCategoryModel(category: Category): CategoryModel {
        val language = LocaleContextHolder.getLocale().language
        return CategoryModel(
            id = category.id,
            level = category.level,
            parentId = category.parentId,
            longTitle = if (language == "en") category.longTitle else (category.longTitleFrench ?: category.longTitle),
            title = if (language == "en") category.title else (category.titleFrench ?: category.title),
        )
    }
}
