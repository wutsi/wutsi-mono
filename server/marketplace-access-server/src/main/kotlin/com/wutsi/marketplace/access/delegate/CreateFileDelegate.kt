package com.wutsi.marketplace.access.delegate

import com.wutsi.marketplace.access.dto.CreateFileRequest
import com.wutsi.marketplace.access.dto.CreateFileResponse
import com.wutsi.marketplace.access.service.FileService
import com.wutsi.platform.core.logging.KVLogger
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
public class CreateFileDelegate(
    private val logger: KVLogger,
    private val service: FileService,
) {
    @Transactional
    public fun invoke(request: CreateFileRequest): CreateFileResponse {
        logger.add("request_url", request.url)
        logger.add("request_product_id", request.productId)
        logger.add("request_content_size", request.contentSize)
        logger.add("request_content_type", request.contentType)

        val file = service.add(request)
        logger.add("response_file_id", file.id)

        return CreateFileResponse(
            fileId = file.id ?: -1,
        )
    }
}
