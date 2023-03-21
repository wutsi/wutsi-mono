package com.wutsi.marketplace.access.service

import com.wutsi.marketplace.access.dao.CategoryRepository
import com.wutsi.marketplace.access.dto.Category
import com.wutsi.marketplace.access.dto.CategorySummary
import com.wutsi.marketplace.access.dto.SearchCategoryRequest
import com.wutsi.marketplace.access.entity.CategoryEntity
import com.wutsi.marketplace.access.error.ErrorURN
import com.wutsi.marketplace.access.util.StringUtil
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.Parameter
import com.wutsi.platform.core.error.ParameterType
import com.wutsi.platform.core.error.exception.NotFoundException
import com.wutsi.platform.core.logging.KVLogger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.net.URL
import java.util.Scanner
import javax.persistence.EntityManager
import javax.persistence.Query

@Service
public class CategoryService(
    private val dao: CategoryRepository,
    private val em: EntityManager,
    private val logger: KVLogger,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(CategoryService::class.java)
    }

    fun findById(id: Long): CategoryEntity =
        dao.findById(id)
            .orElseThrow {
                NotFoundException(
                    error = Error(
                        code = ErrorURN.CATEGORY_NOT_FOUND.urn,
                        parameter = Parameter(
                            name = "id",
                            value = id,
                            type = ParameterType.PARAMETER_TYPE_PATH,
                        ),
                    ),
                )
            }

    fun toCategory(category: CategoryEntity, language: String?) = Category(
        id = category.id,
        title = getTitle(category, language),
        parentId = category.parent?.id,
        level = category.level,
        longTitle = getLongTitle(category, language),
    )

    fun toCategorySummary(category: CategoryEntity, language: String?) = CategorySummary(
        id = category.id,
        title = getTitle(category, language),
        parentId = category.parent?.id,
        level = category.level,
        longTitle = getLongTitle(category, language),
    )

    fun import(): Int {
        return import("en") + import("fr")
    }

    fun import(language: String): Int {
        var rows = 0
        var errors = 0
        val url = getUrl(language)
        val scanner = Scanner(url.readText(charset = Charsets.UTF_8))
        val ids = mutableMapOf<String, Long>()
        while (scanner.hasNextLine()) {
            val line = scanner.nextLine()
            try {
                import(line, ids, language)
            } catch (ex: Exception) {
                LOGGER.warn("$rows: $line", ex)
                errors++
            } finally {
                rows++
            }
        }

        logger.add("rows", rows)
        logger.add("errors", errors)
        return rows - errors
    }

    private fun getUrl(language: String): URL = when (language.lowercase()) {
        "fr" -> URL("https://www.google.com/basepages/producttype/taxonomy-with-ids.fr-FR.txt")
        else -> URL("https://www.google.com/basepages/producttype/taxonomy-with-ids.en-US.txt")
    }

    private fun import(line: String, ids: MutableMap<String, Long>, language: String) {
        if (line.startsWith("#")) { // Comment
            return
        }

        val id = extractId(line)
        val parent = extractParent(line)
        val title = extractTitle(line)
        val longTitle = extractLongTitle(line)
        val level = line.count { it == '>' }
        try {
            save(
                id = id,
                parentId = parent?.let { ids[it] },
                title = title,
                longTitle = longTitle,
                language = language,
                level = level,
            )
        } finally {
            ids[longTitle] = id
        }
    }

    private fun save(
        id: Long,
        title: String,
        longTitle: String,
        level: Int,
        parentId: Long?,
        language: String?,
    ): CategoryEntity {
        val category = dao.findById(id)
            .orElse(CategoryEntity(id = id))

        when (language?.lowercase()) {
            "fr" -> {
                category.titleFrench = title
                category.titleFrenchAscii = StringUtil.toAscii(title)
                category.longTitleFrench = longTitle
            }
            else -> {
                category.title = title
                category.longTitle = longTitle
            }
        }
        category.level = level
        category.parent = parentId?.let {
            dao.findById(it)
                .orElseThrow {
                    NotFoundException(
                        error = Error(
                            code = ErrorURN.PARENT_CATEGORY_NOT_FOUND.urn,
                            parameter = Parameter(
                                name = "parentId",
                                value = it,
                                type = ParameterType.PARAMETER_TYPE_PAYLOAD,
                            ),
                        ),
                    )
                }
        }
        return dao.save(category)
    }

    private fun extractId(line: String): Long {
        val i = line.indexOf("-")
        return line.substring(0, i - 1).toLong()
    }

    private fun extractParent(line: String): String? {
        val i = line.indexOf("-")
        val j = line.lastIndexOf(">")
        return if (j < 0) {
            null
        } else {
            line.substring(i + 1, j - 1).trim()
        }
    }

    private fun extractTitle(line: String): String {
        val j = line.lastIndexOf(">")
        return if (j < 0) {
            val i = line.indexOf("-")
            line.substring(i + 1).trim()
        } else {
            line.substring(j + 1).trim()
        }
    }

    private fun extractLongTitle(line: String): String {
        val i = line.indexOf("-")
        return line.substring(i + 1).trim()
    }

    private fun getTitle(category: CategoryEntity, language: String?) =
        when (language?.lowercase()) {
            "fr" -> category.titleFrench ?: category.title
            else -> category.title
        }

    private fun getLongTitle(category: CategoryEntity, language: String?) =
        when (language?.lowercase()) {
            "fr" -> category.longTitleFrench ?: category.title
            else -> category.longTitle
        } ?: ""

    fun search(request: SearchCategoryRequest, language: String?): List<CategoryEntity> {
        val query = em.createQuery(sql(request, language))
        parameters(request, query)
        return query
            .setFirstResult(request.offset)
            .setMaxResults(request.limit)
            .resultList as List<CategoryEntity>
    }

    private fun sql(request: SearchCategoryRequest, language: String?): String {
        val select = select()
        val where = where(request, language)
        val orderBy = orderBy(language)
        return if (where.isNullOrEmpty()) {
            select
        } else {
            "$select WHERE $where $orderBy"
        }
    }

    private fun select(): String =
        "SELECT a FROM CategoryEntity a"

    private fun orderBy(language: String?): String =
        when (language?.lowercase()) {
            "fr" -> "ORDER BY a.titleFrench"
            else -> "ORDER BY a.title"
        }

    private fun where(request: SearchCategoryRequest, language: String?): String {
        val criteria = mutableListOf<String>()

        if (request.parentId != null) {
            criteria.add("a.parent.id = :parent_id")
        }
        if (request.categoryIds.isNotEmpty()) {
            criteria.add("a.id IN :category_ids")
        }
        if (request.level != null) {
            criteria.add("a.level = :level")
        }
        if (!request.keyword.isNullOrEmpty()) {
            when (language?.lowercase()) {
                "fr" -> criteria.add("((a.titleFrenchAscii IS NULL AND UCASE(a.title) LIKE :keyword) OR (UCASE(a.titleFrenchAscii) LIKE :keyword))")
                else -> criteria.add("UCASE(a.title) LIKE :keyword")
            }
        }
        return criteria.joinToString(separator = " AND ")
    }

    private fun parameters(request: SearchCategoryRequest, query: Query) {
        if (request.parentId != null) {
            query.setParameter("parent_id", request.parentId)
        }
        if (request.categoryIds.isNotEmpty()) {
            query.setParameter("category_ids", request.categoryIds)
        }
        if (!request.keyword.isNullOrEmpty()) {
            query.setParameter("keyword", StringUtil.toAscii(request.keyword).uppercase() + "%")
        }
        if (request.level != null) {
            query.setParameter("level", request.level)
        }
    }
}
