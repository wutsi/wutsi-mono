package com.wutsi.blog.ads.service

import com.wutsi.blog.ads.dao.AdsRepository
import com.wutsi.blog.ads.dao.SearchAdsQueryBuilder
import com.wutsi.blog.ads.domain.AdsEntity
import com.wutsi.blog.ads.dto.AdsAttributeUpdatedEventPayload
import com.wutsi.blog.ads.dto.AdsCTAType
import com.wutsi.blog.ads.dto.AdsStatus
import com.wutsi.blog.ads.dto.AdsType
import com.wutsi.blog.ads.dto.CreateAdsCommand
import com.wutsi.blog.ads.dto.PublishAdsCommand
import com.wutsi.blog.ads.dto.SearchAdsRequest
import com.wutsi.blog.ads.dto.UpdateAdsAttributeCommand
import com.wutsi.blog.error.ErrorCode
import com.wutsi.blog.event.EventPayload
import com.wutsi.blog.event.EventType
import com.wutsi.blog.event.StreamId
import com.wutsi.blog.kpi.dao.AdsKpiRepository
import com.wutsi.blog.kpi.dto.KpiType
import com.wutsi.blog.kpi.dto.TrafficSource
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
import kotlin.jvm.optionals.getOrNull

@Service
class AdsService(
    private val dao: AdsRepository,
    private val adsKpiDao: AdsKpiRepository,
    private val eventStore: EventStore,
    private val eventStream: EventStream,
    private val clock: Clock,
    private val logger: KVLogger,
    private val em: EntityManager,
    private val filterSet: AdsFilterSet,

    @Value("\${wutsi.application.ads.budget-per-impression}") private val budgetPerImpression: Long,
) {
    companion object {
        const val DEFAULT_DURATION: Int = 7
    }

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

    fun findByIds(ids: List<String>): List<AdsEntity> =
        dao.findAllById(ids).toList()

    fun onKpiImported(ad: AdsEntity) {
        ad.totalImpressions = adsKpiDao.sumValueByAdsIdAndTypeAndSource(
            adsId = ad.id ?: "",
            type = KpiType.IMPRESSION,
            source = TrafficSource.ALL
        ) ?: 0
        ad.totalImpressions = adsKpiDao.sumValueByAdsIdAndTypeAndSource(
            adsId = ad.id ?: "",
            type = KpiType.CLICK,
            source = TrafficSource.ALL
        ) ?: 0
        ad.modificationDateTime = Date()
        dao.save(ad)
    }

    @Transactional
    fun onTotalImpressionImported(id: String, impression: Long) {
        val ad = dao.findById(id).getOrNull() ?: return
        ad.todayImpressions = impression
        ad.modificationDateTime = Date()
        dao.save(ad)
    }

    @Transactional
    fun create(command: CreateAdsCommand): AdsEntity {
        logger.add("command", "CreateAdsCommand")
        logger.add("command_title", command.title)
        logger.add("command_type", command.type)
        logger.add("command_currency", command.currency)

        val tomorrow = DateUtils.addDays(Date(clock.millis()), 1)
        val ads = dao.save(
            AdsEntity(
                id = UUID.randomUUID().toString(),
                title = command.title,
                type = command.type,
                userId = command.userId,
                startDate = tomorrow,
                endDate = DateUtils.addDays(tomorrow, DEFAULT_DURATION),
                currency = command.currency,
            )
        )

        notify(ads, EventType.ADS_CREATED_EVENT, command.timestamp)
        return ads
    }

    @Transactional
    fun publish(command: PublishAdsCommand) {
        logger.add("command", "StartAdsCommand")
        logger.add("command_id", command.id)

        val ads = findById(command.id)
        if (ads.status != AdsStatus.DRAFT) {
            conflict(ads, ErrorCode.ADS_NOT_IN_DRAFT)
        }
        validatePublish(ads)

        val now = Date(clock.millis())
        ads.status = AdsStatus.PUBLISHED
        ads.publishedDateTime = now
        ads.modificationDateTime = now
        dao.save(ads)

        notify(ads, EventType.ADS_PUBLISHED_EVENT, command.timestamp)
    }

    @Transactional
    fun start(ads: AdsEntity): Boolean {
        if (ads.status != AdsStatus.PUBLISHED) {
            return false
        }

        val now = Date(clock.millis())
        val days = DateUtils.daysBetween(ads.startDate!!, ads.endDate!!)

        ads.status = AdsStatus.RUNNING
        ads.maxImpressions = ads.budget / budgetPerImpression
        ads.maxDailyImpressions = ads.maxImpressions / days
        ads.modificationDateTime = now
        dao.save(ads)

        notify(ads, EventType.ADS_STARTED_EVENT, now.time)
        return true
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
        } else if ("cta_type" == lname) {
            ads.ctaType = value?.let { AdsCTAType.valueOf(value.uppercase()) } ?: AdsCTAType.UNKNOWN
        } else if ("url" == lname) {
            ads.url = value
        } else if ("image_url" == lname) {
            ads.imageUrl = value
        } else if ("type" == lname) {
            ads.type = value?.let { AdsType.valueOf(value.uppercase()) } ?: AdsType.UNKNOWN
        } else if ("start_date" == lname) {
            val date = value?.let { SimpleDateFormat("yyyy-MM-dd").parse(value) }
                ?: DateUtils.toDate(LocalDate.now().plusDays(1))
            ads.startDate = date
        } else if ("end_date" == lname) {
            ads.endDate = value?.let { SimpleDateFormat("yyyy-MM-dd").parse(value) }
        } else if ("budget" == lname) {
            ads.budget = value?.toLong() ?: 0L
        } else {
            throw ConflictException(Error(ErrorCode.ADS_ATTRIBUTE_INVALID))
        }

        ads.modificationDateTime = Date()
        return dao.save(ads)
    }

    private fun validatePublish(ads: AdsEntity) {
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
        if (ads.startDate == null) {
            throw conflict(ads, ErrorCode.ADS_START_DATE_MISSING)
        }
        if (ads.endDate == null) {
            throw conflict(ads, ErrorCode.ADS_END_DATE_MISSING)
        } else if (!ads.endDate!!.after(ads.startDate)) {
            throw conflict(ads, ErrorCode.ADS_END_DATE_BEFORE_START_DATE)
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