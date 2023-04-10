package com.wutsi.application.web.endpoint

import com.wutsi.application.web.model.Mapper
import com.wutsi.application.web.model.MemberModel
import com.wutsi.application.web.service.MerchantHolder
import com.wutsi.checkout.manager.CheckoutManagerApi
import com.wutsi.enums.BusinessStatus
import com.wutsi.enums.DeviceType
import com.wutsi.enums.util.ChannelDetector
import com.wutsi.error.ErrorURN
import com.wutsi.marketplace.manager.MarketplaceManagerApi
import com.wutsi.membership.manager.MembershipManagerApi
import com.wutsi.membership.manager.dto.Member
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.exception.NotFoundException
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.regulation.RegulationEngine
import feign.FeignException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ModelAttribute
import ua_parser.Parser
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

abstract class AbstractController {
    @Autowired
    protected lateinit var membershipManagerApi: MembershipManagerApi

    @Autowired
    protected lateinit var marketplaceManagerApi: MarketplaceManagerApi

    @Autowired
    protected lateinit var checkoutManagerApi: CheckoutManagerApi

    @Autowired
    protected lateinit var regulationEngine: RegulationEngine

    @Autowired
    protected lateinit var mapper: Mapper

    @Value("\${wutsi.application.asset-url}")
    protected lateinit var assetUrl: String

    @Value("\${wutsi.application.server-url}")
    protected lateinit var serverUrl: String

    @Value("\${wutsi.application.google.analytics.id}")
    protected lateinit var gaId: String

    @Autowired
    protected lateinit var request: HttpServletRequest

    @Autowired
    protected lateinit var logger: KVLogger

    @Autowired
    protected lateinit var merchantHolder: MerchantHolder

    @ModelAttribute("assetUrl")
    fun assetUrl() = assetUrl

    @ModelAttribute("gaId")
    fun googleAnalyticsId() = if (gaId.isNullOrEmpty()) null else gaId

    private val channelDetector = ChannelDetector()
    private val uaParser = Parser()

    protected fun resolveCurrentMerchant(id: Long): MemberModel =
        validateMerchant(
            membershipManagerApi.getMember(id).member,
        )

    protected fun resolveCurrentMerchant(name: String): MemberModel =
        validateMerchant(
            membershipManagerApi.getMemberByName(name).member,
        )

    private fun validateMerchant(merchant: Member): MemberModel {
        if (!merchant.active) { // Must be active
            throw NotFoundException(
                error = Error(
                    code = ErrorURN.MEMBER_NOT_ACTIVE.urn,
                ),
            )
        }
        if (!merchant.business) { // Must be a business account
            throw NotFoundException(
                error = Error(
                    code = ErrorURN.MEMBER_NOT_BUSINESS.urn,
                ),
            )
        }

        val business = merchant.businessId?.let { checkoutManagerApi.getBusiness(it).business }
        if (business?.status != BusinessStatus.ACTIVE.name) {
            throw NotFoundException(
                error = Error(
                    code = ErrorURN.BUSINESS_NOT_ACTIVE.urn,
                ),
            )
        }

        val store = merchant.storeId?.let { marketplaceManagerApi.getStore(it).store }
        val fundraising = merchant.fundraisingId?.let { marketplaceManagerApi.getFundraising(it).fundraising }

        merchantHolder.set(merchant)
        return mapper.toMemberModel(merchant, business, store, fundraising)
    }

    @ExceptionHandler(FeignException::class)
    fun onFeignException(response: HttpServletResponse, ex: FeignException) {
        logger.setException(ex)
        response.sendError(ex.status(), ex.message)
    }

    @ExceptionHandler(NotFoundException::class)
    fun onNotFoundException(response: HttpServletResponse, ex: NotFoundException) {
        logger.setException(ex)
        response.sendError(404, ex.message)
    }

    @ExceptionHandler(Throwable::class)
    fun onThrowable(response: HttpServletResponse, ex: Throwable) {
        logger.setException(ex)
        response.sendError(500, ex.message)
    }

    protected fun toDeviceType(ua: String): DeviceType? {
        val client = uaParser.parse(ua)
        return if (client.device?.family.equals("spider", true)) { // Bot
            null
        } else if (client.userAgent.family?.contains("mobile", true) == true) {
            DeviceType.MOBILE
        } else {
            DeviceType.DESKTOP
        }
    }

    protected fun toChannelType(ua: String) =
        channelDetector.detect("", "", ua)
}
