package com.wutsi.blog.product.service

import com.wutsi.blog.error.ErrorCode
import com.wutsi.blog.product.dao.CategoryRepository
import com.wutsi.blog.product.dao.SearchCategoryQueryBuilder
import com.wutsi.blog.product.domain.CategoryEntity
import com.wutsi.blog.product.dto.SearchCategoryRequest
import com.wutsi.blog.util.Predicates
import com.wutsi.blog.util.StringUtils
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.Parameter
import com.wutsi.platform.core.error.ParameterType
import com.wutsi.platform.core.error.exception.NotFoundException
import com.wutsi.platform.core.logging.KVLogger
import jakarta.persistence.EntityManager
import org.apache.commons.io.IOUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Scanner

@Service
class CategoryService(
    private val dao: CategoryRepository,
    private val logger: KVLogger,
    private val em: EntityManager,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(CategoryService::class.java)
    }

    fun findById(id: Long): CategoryEntity =
        dao.findById(id)
            .orElseThrow {
                NotFoundException(
                    error = Error(
                        code = ErrorCode.CATEGORY_NOT_FOUND,
                        parameter = Parameter(
                            name = "id",
                            value = id
                        )
                    )
                )
            }

    fun all(): List<CategoryEntity> =
        dao.findAll().toList()

    fun search(request: SearchCategoryRequest): List<CategoryEntity> {
        logger.add("request_level", request.level)
        logger.add("request_category_ids", request.categoryIds)
        logger.add("request_keywords", request.keyword)
        logger.add("request_language", request.language)
        logger.add("request_limit", request.limit)
        logger.add("request_offset", request.offset)
        logger.add("request_parent_id", request.parentId)

        val builder = SearchCategoryQueryBuilder()
        val sql = builder.query(request)
        val params = builder.parameters(request)
        val query = em.createNativeQuery(sql, CategoryEntity::class.java)
        Predicates.setParameters(query, params)
        return query.resultList as List<CategoryEntity>
    }

    @Transactional
    fun import(): Int {
        return import("en") + import("fr")
    }

    fun import(language: String): Int {
        var rows = 0
        var errors = 0
        val data = IOUtils.toString(
            CategoryService::class.java.getResourceAsStream("/data/category-$language.txt"),
            "utf-8"
        )
        val scanner = Scanner(data)
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

        logger.add("import_rows_$language", rows)
        logger.add("import_errors_$language", errors)
        return rows - errors
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
                category.titleFrenchAscii = StringUtils.toAscii(title)
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
                            code = ErrorCode.CATEGORY_PARENT_NOT_FOUND,
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
}
