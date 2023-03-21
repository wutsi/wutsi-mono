package com.wutsi.marketplace.access.service

import com.wutsi.enums.MeetingProviderType
import com.wutsi.marketplace.access.dao.MeetingProviderRepository
import com.wutsi.marketplace.access.dto.MeetingProviderSummary
import com.wutsi.marketplace.access.entity.MeetingProviderEntity
import com.wutsi.marketplace.access.entity.ProductEntity
import com.wutsi.marketplace.access.error.ErrorURN
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.exception.NotFoundException
import org.springframework.stereotype.Service

@Service
class MeetingProviderService(
    private val dao: MeetingProviderRepository,
) {
    fun search(): List<MeetingProviderEntity> =
        dao.findAll().toList()

    fun findById(id: Long) =
        dao.findById(id).orElseThrow {
            NotFoundException(
                error = Error(
                    code = ErrorURN.MEETING_PROVIDER_NOT_FOUND.urn,
                ),
            )
        }

    fun toMeetingProviderSummary(provider: MeetingProviderEntity) = MeetingProviderSummary(
        id = provider.id ?: -1,
        name = provider.name,
        type = provider.type.name,
        logoUrl = provider.logoUrl,
    )

    fun toJoinUrl(product: ProductEntity): String? =
        product.eventMeetingId?.let {
            when (product.eventMeetingProvider?.type) {
                MeetingProviderType.ZOOM -> "https://us04web.zoom.us/meeting/${product.eventMeetingId}"
                MeetingProviderType.MEET -> "https://meet.google.com/${product.eventMeetingId}"
                else -> null
            }
        }
}
