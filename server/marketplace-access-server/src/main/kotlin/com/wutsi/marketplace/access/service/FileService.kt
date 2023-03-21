package com.wutsi.marketplace.access.service

import com.wutsi.marketplace.access.dao.FileRepository
import com.wutsi.marketplace.access.dao.ProductRepository
import com.wutsi.marketplace.access.dto.CreateFileRequest
import com.wutsi.marketplace.access.dto.FileSummary
import com.wutsi.marketplace.access.entity.FileEntity
import com.wutsi.marketplace.access.error.ErrorURN
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.Parameter
import com.wutsi.platform.core.error.ParameterType
import com.wutsi.platform.core.error.exception.NotFoundException
import org.springframework.stereotype.Service
import java.time.ZoneOffset
import java.util.Date

@Service
public class FileService(
    private val dao: FileRepository,
    private val productDao: ProductRepository,
) {
    fun findById(id: Long): FileEntity {
        val file = dao.findById(id)
            .orElseThrow {
                NotFoundException(
                    error = Error(
                        code = ErrorURN.FILE_NOT_FOUND.urn,
                        parameter = Parameter(
                            name = "id",
                            value = id,
                            type = ParameterType.PARAMETER_TYPE_PATH,
                        ),
                    ),
                )
            }
        if (file.isDeleted) {
            throw NotFoundException(
                error = Error(
                    code = ErrorURN.FILE_DELETED.urn,
                    parameter = Parameter(
                        name = "id",
                        value = id,
                        type = ParameterType.PARAMETER_TYPE_PATH,
                    ),
                ),
            )
        }
        return file
    }

    fun add(request: CreateFileRequest): FileEntity =
        dao.save(
            FileEntity(
                product = productDao.findById(request.productId).get(),
                url = request.url,
                name = request.name,
                created = Date(),
                contentType = request.contentType,
                contentSize = request.contentSize,
            ),
        )

    fun delete(id: Long) {
        val opt = dao.findById(id)
        if (opt.isPresent && !opt.get().isDeleted) {
            val file = opt.get()
            file.deleted = Date()
            file.isDeleted = true
            dao.save(file)
        }
    }

    fun toFileSummary(file: FileEntity) = FileSummary(
        id = file.id ?: -1,
        url = file.url,
        name = file.name,
        contentType = file.contentType,
        contentSize = file.contentSize,
        created = file.created.toInstant().atOffset(ZoneOffset.UTC),
    )
}
