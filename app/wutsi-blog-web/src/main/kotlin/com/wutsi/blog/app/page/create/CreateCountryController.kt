package com.wutsi.blog.app.page.create

import com.wutsi.blog.app.backend.IpApiBackend
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.UserService
import com.wutsi.blog.app.util.PageName
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/create/country")
class CreateCountryController(
    userService: UserService,
    requestContext: RequestContext,
    private val ip: IpApiBackend,
) : AbstractCreateController(userService, requestContext) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(CreateCountryController::class.java)
    }

    override fun pageName() = PageName.CREATE_COUNTRY
    override fun pagePath() = "create/country"
    override fun redirectUrl() = "/create/review"
    override fun attributeName() = "country"
    override fun value() = requestContext.currentUser()?.country ?: getCountry()

    private fun getCountry() =
        try {
            ip.resolve(requestContext.remoteIp()).countryCode
        } catch (ex: Exception) {
            LOGGER.warn("Unable to resolve the country", ex)
            null
        }
}
