package com.wutsi.marketplace.access.delegate

import com.wutsi.marketplace.access.dto.SearchPictureRequest
import com.wutsi.marketplace.access.dto.SearchPictureResponse
import com.wutsi.marketplace.access.service.PictureService
import org.springframework.stereotype.Service

@Service
class SearchPictureDelegate(private val service: PictureService) {
    fun invoke(request: SearchPictureRequest): SearchPictureResponse {
        val pictures = service.search(request)
        return SearchPictureResponse(
            pictures = pictures.map { service.toPictureSummary(it) },
        )
    }
}
