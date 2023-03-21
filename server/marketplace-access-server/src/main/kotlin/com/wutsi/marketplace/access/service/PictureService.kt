package com.wutsi.marketplace.access.service

import com.wutsi.marketplace.access.dao.PictureRepository
import com.wutsi.marketplace.access.dto.CreatePictureRequest
import com.wutsi.marketplace.access.dto.PictureSummary
import com.wutsi.marketplace.access.dto.SearchPictureRequest
import com.wutsi.marketplace.access.entity.PictureEntity
import com.wutsi.marketplace.access.entity.ProductEntity
import com.wutsi.marketplace.access.error.ErrorURN
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.Parameter
import com.wutsi.platform.core.error.exception.NotFoundException
import org.apache.commons.codec.digest.DigestUtils
import org.springframework.stereotype.Service
import java.util.Date
import javax.persistence.EntityManager
import javax.persistence.Query

@Service
class PictureService(
    private val dao: PictureRepository,
    private val em: EntityManager,
) {
    fun create(product: ProductEntity, url: String): PictureEntity =
        dao.save(
            PictureEntity(
                product = product,
                url = url.trim(),
                hash = hash(url.lowercase()),
            ),
        )

    fun create(product: ProductEntity, request: CreatePictureRequest): PictureEntity =
        create(product, request.url)

    fun delete(id: Long): PictureEntity {
        val picture = findById(id)
        picture.isDeleted = true
        picture.deleted = Date()
        return dao.save(picture)
    }

    fun findById(id: Long): PictureEntity {
        val picture = dao.findById(id)
            .orElseThrow {
                NotFoundException(
                    error = Error(
                        code = ErrorURN.PICTURE_NOT_FOUND.urn,
                        parameter = Parameter(
                            name = "id",
                            value = id,
                        ),
                    ),
                )
            }
        if (picture.isDeleted) {
            throw NotFoundException(
                error = Error(
                    code = ErrorURN.PICTURE_DELETED.urn,
                    parameter = Parameter(
                        name = "id",
                        value = id,
                    ),
                ),
            )
        }
        return picture
    }

    fun toPictureSummary(picture: PictureEntity) = PictureSummary(
        id = picture.id ?: -1,
        url = picture.url,
    )

    fun search(request: SearchPictureRequest): List<PictureEntity> {
        val sql = sql(request)
        val query = em.createQuery(sql)
        parameters(request, query)
        return query
            .setFirstResult(request.offset)
            .setMaxResults(request.limit)
            .resultList as List<PictureEntity>
    }

    private fun sql(request: SearchPictureRequest): String {
        val select = select()
        val where = where(request)
        return if (where.isNullOrEmpty()) {
            select
        } else {
            "$select WHERE $where"
        }
    }

    private fun select(): String =
        "SELECT P FROM PictureEntity P"

    private fun where(request: SearchPictureRequest): String {
        val criteria = mutableListOf("P.isDeleted=false") // Picture not deleted
        criteria.add("P.product.isDeleted=false") // Product not deleted

        if (request.pictureIds.isNotEmpty()) {
            criteria.add("P.id IN :picture_ids")
        }
        if (request.productIds.isNotEmpty()) {
            criteria.add("P.product.id IN :product_ids")
        }
        if (request.pictureUrls.isNotEmpty()) {
            criteria.add("P.hash IN :hashes")
        }

        return criteria.joinToString(separator = " AND ")
    }

    private fun parameters(request: SearchPictureRequest, query: Query) {
        if (request.pictureIds.isNotEmpty()) {
            query.setParameter("picture_ids", request.pictureIds)
        }
        if (request.productIds.isNotEmpty()) {
            query.setParameter("product_ids", request.productIds)
        }
        if (request.pictureUrls.isNotEmpty()) {
            query.setParameter("hashes", request.pictureUrls.map { hash(it) })
        }
    }

    private fun hash(url: String): String =
        DigestUtils.md5Hex(url.lowercase()).lowercase()
}
