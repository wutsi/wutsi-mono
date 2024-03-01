package com.wutsi.blog.ads.service

import com.wutsi.blog.ads.dao.AdsRepository
import com.wutsi.blog.ads.dao.SearchAdsQueryBuilder
import com.wutsi.blog.ads.domain.AdsEntity
import com.wutsi.blog.ads.dto.AdsAttributeUpdatedEventPayload
import com.wutsi.blog.ads.dto.AdsCTAType
import com.wutsi.blog.ads.dto.AdsStatus
import com.wutsi.blog.ads.dto.AdsType
import com.wutsi.blog.ads.dto.CreateAdsCommand
import com.wutsi.blog.ads.dto.SearchAdsRequest
import com.wutsi.blog.ads.dto.StartAdsCommand
import com.wutsi.blog.ads.dto.UpdateAdsAttributeCommand
import com.wutsi.blog.error.ErrorCode
import com.wutsi.blog.event.EventPayload
import com.wutsi.blog.event.EventType
import com.wutsi.blog.event.StreamId
import com.wutsi.blog.util.DateUtils
import com.wutsi.blog.util.Predicates
import com.wutsi.event.store.Event
import com.wutsi.event.store.EventStore
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.Parameter
import com.wutsi.platform.core.error.exception.ConflictException
import com.wutsi.platform.core.error.exception.NotFoundException
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.stream.EventStream
import jakarta.persistence.EntityManager
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.text.SimpleDateFormat
import java.time.Clock
import java.time.LocalDate
import java.util.Date
import java.util.UUID

@Service
class AdsService(
    private val dao: AdsRepository,
    private val eventStore: EventStore,
    private val eventStream: EventStream,
    private val clock: Clock,
    private val logger: KVLogger,
    private val em: EntityManager,
    private val filterSet: AdsFilterSet,

    @Value("\${wutsi.application.ads.budget-per-impression}") private val budgetPerImpression: Long,
) {
    fun findById(id: String): AdsEntity =
        dao.findById(id)
            .orElseThrow {
                NotFoundException(
                    error = Error(
                        code = ErrorCode.ADS_NOT_FOUND,
                        parameter = Parameter(
                            name = "id",
                            value = id
                        )
                    )
                )
            }

    @Transactional
    fun create(command: CreateAdsCommand): AdsEntity {
        logger.add("command", "CreateAdsCommand")
        logger.add("command_title", command.title)
        logger.add("command_type", command.type)

        val ads = dao.save(
            AdsEntity(
                id = UUID.randomUUID().toString(),
                title = command.title,
                type = command.type,
                userId = command.userId,
                durationDays = 1,
                startDate = DateUtils.toDate(LocalDate.now().plusDays(1))
            )
        )

        notify(ads, EventType.ADS_CREATED_EVENT, command.timestamp)
        return ads
    }

    @Transactional
    fun start(command: StartAdsCommand) {
        logger.add("command", "StartAdsCommand")
        logger.add("command_id", command.id)

        val ads = findById(command.id)
        validateStart(ads)

        val now = Date(clock.millis())
        ads.status = AdsStatus.RUNNING
        ads.endDate = DateUtils.addDays(ads.startDate, ads.durationDays)
        ads.maxImpressions = ads.budget / budgetPerImpression
        ads.maxDailyImpressions = ads.maxImpressions / ads.durationDays
        ads.modificationDateTime = now
        dao.save(ads)

        notify(ads, EventType.ADS_STARTED_EVENT, command.timestamp)
    }

    @Transactional
    fun complete(ads: AdsEntity): Boolean {
        if (ads.status == AdsStatus.RUNNING) {
            val now = Date(clock.millis())
            ads.status = AdsStatus.COMPLETED
            ads.completedDateTime = now
            dao.save(ads)

            notify(ads, EventType.ADS_COMPLETED_EVENT, now.time)
            return true
        } else {
            return false
        }
    }

    fun search(request: SearchAdsRequest): List<AdsEntity> {
        logger.add("request_user_id", request.userId)
        logger.add("request_type", request.type)
        logger.add("request_status", request.status)
        logger.add("request_start_date_from", request.startDateFrom)
        logger.add("request_start_date_to", request.startDateTo)
        logger.add("request_end_date_from", request.endDateFrom)
        logger.add("request_end_date_to", request.endDateTo)
        logger.add("request_limit", request.limit)
        logger.add("request_offset", request.offset)

        return searchAds(request)
    }

    fun searchAds(request: SearchAdsRequest): List<AdsEntity> {
        val builder = SearchAdsQueryBuilder()
        val sql = builder.query(request)
        val params = builder.parameters(request)
        val query = em.createNativeQuery(sql, AdsEntity::class.java)
        Predicates.setParameters(query, params)

        val ads = query.resultList as List<AdsEntity>
        return filterSet.filter(request, ads)
    }

    @Transactional
    fun updateAttribute(command: UpdateAdsAttributeCommand) {
        logger.add("command_ads_id", command.adsId)
        logger.add("command_name", command.name)
        logger.add("command_value", command.value)

        val ads = set(command.adsId, command.name, command.value)

        val payload = AdsAttributeUpdatedEventPayload(
            name = command.name,
            value = command.value,
        )
        notify(ads, EventType.ADS_ATTRIBUTE_UPDATED_EVENT, command.timestamp, payload)
    }

    private fun set(id: String, name: String, value: String?): AdsEntity {
        val ads = findById(id)
        val lname = name.lowercase()

        if ("title" == lname) {
            ads.title = value ?: ""
        } else if ("duration_days" == lname) {
            ads.durationDays = value?.toInt() ?: 1
        } else if ("cta_type" == lname) {
            ads.ctaType = value?.let { AdsCTAType.valueOf(value.uppercase()) } ?: AdsCTAType.UNKNOWN
        } else if ("url" == lname) {
            ads.url = value
        } else if ("image_url" == lname) {
            ads.imageUrl = value
        } else if ("type" == lname) {
            ads.type = value?.let { AdsType.valueOf(value.uppercase()) } ?: AdsType.UNKNOWN
        } else if ("start_date" == lname) {
            ads.startDate = value?.let { SimpleDateFormat("yyyy-MM-dd").parse(value) }
                ?: DateUtils.toDate(LocalDate.now().plusDays(1))
        } else if ("budget" == lname) {
            ads.budget = value?.toLong() ?: 0L
        } else {
            throw ConflictException(Error(ErrorCode.ADS_ATTRIBUTE_INVALID))
        }

        ads.modificationDateTime = Date()
        return dao.save(ads)
    }

    private fun validateStart(ads: AdsEntity) {
        if (ads.status != AdsStatus.DRAFT) {
            throw conflict(ads, ErrorCode.ADS_NOT_IN_DRAFT)
        }
        if (ads.imageUrl.isNullOrEmpty()) {
            throw conflict(ads, ErrorCode.ADS_IMAGE_URL_MISSING)
        }
        if (ads.url.isNullOrEmpty()) {
            throw conflict(ads, ErrorCode.ADS_URL_MISSING)
        }
        if (ads.budget <= 0) {
            throw conflict(ads, ErrorCode.ADS_BUDGET_MISSING)
        }
    }

    private fun conflict(ads: AdsEntity, error: String) = ConflictException(
        error = Error(
            code = error,
            parameter = Parameter(
                name = "id",
                value = ads.id
            )
        )
    )

    private fun notify(ads: AdsEntity, type: String, timestamp: Long, payload: Any? = null) {
        val eventId = eventStore.store(
            Event(
                streamId = StreamId.ADS,
                type = type,
                entityId = ads.id!!,
                timestamp = Date(timestamp),
                payload = payload
            ),
        )

        eventStream.enqueue(type, EventPayload(eventId = eventId))
    }
}