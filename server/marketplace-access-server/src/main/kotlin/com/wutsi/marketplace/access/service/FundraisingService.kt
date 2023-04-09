package com.wutsi.marketplace.access.service

import com.wutsi.enums.FundraisingStatus
import com.wutsi.marketplace.access.dao.FundraisingRepository
import com.wutsi.marketplace.access.dto.CreateFundraisingRequest
import com.wutsi.marketplace.access.dto.Fundraising
import com.wutsi.marketplace.access.dto.UpdateFundraisingAttributeRequest
import com.wutsi.marketplace.access.dto.UpdateFundraisingStatusRequest
import com.wutsi.marketplace.access.entity.FundraisingEntity
import com.wutsi.marketplace.access.error.ErrorURN
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.Parameter
import com.wutsi.platform.core.error.ParameterType
import com.wutsi.platform.core.error.exception.BadRequestException
import com.wutsi.platform.core.error.exception.NotFoundException
import org.springframework.stereotype.Service
import java.time.ZoneOffset
import java.util.Date

@Service
class FundraisingService(
    private val dao: FundraisingRepository,
) {
    fun findById(id: Long): FundraisingEntity =
        dao.findById(id)
            .orElseThrow {
                NotFoundException(
                    error = Error(
                        code = ErrorURN.FUNDRAISING_NOT_FOUND.urn,
                        parameter = Parameter(
                            name = "id",
                            value = id,
                            type = ParameterType.PARAMETER_TYPE_PATH,
                        ),
                    ),
                )
            }

    fun create(request: CreateFundraisingRequest): FundraisingEntity {
        val fundraisings =
            dao.findByAccountIdAndStatusNotIn(
                request.accountId,
                listOf(FundraisingStatus.UNKNOWN, FundraisingStatus.INACTIVE),
            )
        return if (fundraisings.isEmpty()) {
            dao.save(
                FundraisingEntity(
                    accountId = request.accountId,
                    businessId = request.businessId,
                    currency = request.currency,
                    status = FundraisingStatus.ACTIVE,
                    amount = request.amount,
                ),
            )
        } else {
            fundraisings[0]
        }
    }

    fun updateStatus(id: Long, request: UpdateFundraisingStatusRequest) {
        val fundraising = findById(id)
        val status = FundraisingStatus.valueOf(request.status)
        if (fundraising.status == status) {
            return
        }

        fundraising.status = status
        fundraising.updated = Date()
        when (status) {
            FundraisingStatus.INACTIVE -> fundraising.deactivated = Date()
            FundraisingStatus.UNDER_REVIEW -> fundraising.deactivated = null
            FundraisingStatus.ACTIVE -> fundraising.deactivated = null
            else -> throw BadRequestException(
                error = Error(
                    code = ErrorURN.STATUS_NOT_VALID.urn,
                    parameter = Parameter(
                        name = "status",
                        value = request.status,
                        type = ParameterType.PARAMETER_TYPE_PAYLOAD,
                    ),
                ),
            )
        }
        dao.save(fundraising)
    }

    fun toFundraising(fundraising: FundraisingEntity) = Fundraising(
        id = fundraising.id ?: -1,
        accountId = fundraising.accountId,
        businessId = fundraising.businessId,
        created = fundraising.created.toInstant().atOffset(ZoneOffset.UTC),
        updated = fundraising.updated.toInstant().atOffset(ZoneOffset.UTC),
        deactivated = fundraising.deactivated?.toInstant()?.atOffset(ZoneOffset.UTC),
        currency = fundraising.currency,
        status = fundraising.status.name,
        amount = fundraising.amount,
        videoUrl = fundraising.videoUrl,
        description = fundraising.description,
    )

    fun updateAttribute(id: Long, request: UpdateFundraisingAttributeRequest) {
        val fundraising = findById(id)
        when (request.name.lowercase()) {
            "description" -> fundraising.description = toString(request.value) ?: "NO TITLE"
            "video-url" -> fundraising.videoUrl = toString(request.value)
            "amount" -> fundraising.amount = toLong(request.value) ?: 0
            else -> throw BadRequestException(
                error = Error(
                    code = ErrorURN.ATTRIBUTE_NOT_VALID.urn,
                    parameter = Parameter(
                        name = "name",
                        value = request.name,
                        type = ParameterType.PARAMETER_TYPE_PAYLOAD,
                    ),
                ),
            )
        }
        fundraising.updated = Date()
        dao.save(fundraising)
    }

    private fun toString(value: String?): String? =
        if (value.isNullOrEmpty()) {
            null
        } else {
            value
        }

    private fun toLong(value: String?): Long? =
        if (value.isNullOrEmpty()) {
            null
        } else {
            value.toLong()
        }
}
