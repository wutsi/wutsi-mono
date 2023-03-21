package com.wutsi.membership.access.service

import com.wutsi.membership.access.dao.CategoryRepository
import com.wutsi.membership.access.dto.Category
import com.wutsi.membership.access.dto.CategorySummary
import com.wutsi.membership.access.dto.SaveCategoryRequest
import com.wutsi.membership.access.dto.SearchCategoryRequest
import com.wutsi.membership.access.entity.CategoryEntity
import com.wutsi.membership.access.error.ErrorURN
import com.wutsi.membership.access.util.StringUtil
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.Parameter
import com.wutsi.platform.core.error.ParameterType
import com.wutsi.platform.core.error.exception.NotFoundException
import com.wutsi.platform.core.logging.DefaultKVLogger
import com.wutsi.platform.core.logging.KVLogger
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVRecord
import org.springframework.stereotype.Service
import javax.persistence.EntityManager
import javax.persistence.Query

@Service
class CategoryService(
    private val dao: CategoryRepository,
    private val em: EntityManager,
    private val logger: KVLogger,
) {
    fun findById(id: Long): CategoryEntity =
        dao.findById(id).orElseThrow {
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

    fun save(id: Long, request: SaveCategoryRequest, language: String?): CategoryEntity {
        val category = dao.findById(id)
            .orElse(CategoryEntity(id = id))

        when (language?.lowercase()) {
            "fr" -> {
                category.titleFrench = request.title
                category.titleFrenchAscii = StringUtil.toAscii(request.title)
            }
            else -> category.title = request.title
        }
        return dao.save(category)
    }

    private fun getTitle(category: CategoryEntity, language: String?): String =
        when (language?.lowercase()) {
            "fr" -> category.titleFrench ?: category.title
            else -> category.title
        }

    fun toCategory(category: CategoryEntity, language: String?) = Category(
        id = category.id,
        title = getTitle(category, language),
    )

    fun toCategorySummary(category: CategoryEntity, language: String?) = CategorySummary(
        id = category.id,
        title = getTitle(category, language),
    )

    fun search(request: SearchCategoryRequest, language: String?): List<CategoryEntity> {
        val query = em.createQuery(sql(request, language))
        parameters(request, query)
        return query
            .setFirstResult(request.offset)
            .setMaxResults(request.limit)
            .resultList as List<CategoryEntity>
    }

    fun import(language: String) {
        var row = 1
        var imported = 0
        var errors = 0
        val input = CategoryService::class.java.getResourceAsStream("/data/business-categories.csv")
        val parser = CSVParser.parse(
            input,
            Charsets.UTF_8,
            CSVFormat.Builder.create()
                .setSkipHeaderRecord(true)
                .setDelimiter(",")
                .setHeader("id", "title", "title_fr")
                .build(),
        )

        for (record in parser) {
            val logger = DefaultKVLogger()
            log(row, record, logger)
            try {
                save(record, language)
                imported++
            } catch (ex: Exception) {
                errors++
                logger.setException(ex)
            } finally {
                logger.log()
                row++
            }
        }

        logger.add("csv_rows", row)
        logger.add("csv_imported", imported)
        logger.add("csv_errors", errors)
    }

    private fun save(record: CSVRecord, language: String?) {
        save(
            id = record.get("id").toLong(),
            request = SaveCategoryRequest(
                title = if (language == "fr") {
                    record.get("title_fr")
                } else {
                    record.get("title")
                },
            ),
            language = language,
        )
    }

    private fun log(row: Int, record: CSVRecord, logger: KVLogger) {
        logger.add("row", row)
        logger.add("record_id", record.get("id"))
        logger.add("record_title", record.get("title"))
        logger.add("record_title_fr", record.get("title_fr"))
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

        if (request.categoryIds.isNotEmpty()) {
            criteria.add("a.id IN :category_ids")
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
        if (request.categoryIds.isNotEmpty()) {
            query.setParameter("category_ids", request.categoryIds)
        }
        if (!request.keyword.isNullOrEmpty()) {
            query.setParameter("keyword", StringUtil.toAscii(request.keyword).uppercase() + "%")
        }
    }
}
