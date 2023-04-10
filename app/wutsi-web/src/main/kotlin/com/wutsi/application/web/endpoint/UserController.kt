package com.wutsi.application.web.endpoint

import com.wutsi.application.web.Page
import com.wutsi.application.web.model.MemberModel
import com.wutsi.application.web.model.OfferModel
import com.wutsi.application.web.model.PageModel
import com.wutsi.enums.ProductSort
import com.wutsi.marketplace.manager.dto.Fundraising
import com.wutsi.marketplace.manager.dto.SearchOfferRequest
import com.wutsi.marketplace.manager.dto.Store
import com.wutsi.membership.manager.dto.Member
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping
class UserController : AbstractController() {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(UserController::class.java)
    }

    @GetMapping("/@{name}")
    fun index(@PathVariable name: String, model: Model): String =
        render(
            resolveCurrentMerchant(name),
            model,
        )

    @GetMapping("/u/{id}")
    fun index(@PathVariable id: Long, model: Model): String =
        render(
            resolveCurrentMerchant(id),
            model,
        )

    private fun render(merchant: MemberModel, model: Model): String {
        model.addAttribute("member", merchant)
        model.addAttribute("page", createPage(merchant))
        model.addAttribute("offers", findFeatureOffers(merchant))

        return "user"
    }

    private fun createPage(member: MemberModel) = PageModel(
        name = Page.PROFILE,
        title = member.displayName,
        description = member.biography,
        url = "$serverUrl${member.url}",
        imageUrl = member.pictureUrl,
        sitemapUrl = "$serverUrl/sitemap.xml?id=${member.id}",
        twitterUserId = member.twitterId,
    )

    private fun findFeatureOffers(member: MemberModel): List<OfferModel> {
        if (member.storeId == null) {
            return emptyList()
        }

        return try {
            marketplaceManagerApi.searchOffer(
                request = SearchOfferRequest(
                    storeId = member.storeId,
                    limit = 4,
                    sortBy = ProductSort.RECOMMENDED.name,
                ),
            ).offers.map {
                mapper.toOfferModel(it, member)
            }
        } catch (ex: Exception) {
            LOGGER.warn("Unable to resolve offers", ex)
            emptyList()
        }
    }

    private fun findFundraising(member: Member): Fundraising? =
        member.fundraisingId?.let {
            try {
                marketplaceManagerApi.getFundraising(member.fundraisingId!!).fundraising
            } catch (ex: Exception) {
                LOGGER.warn("Unable to resolve Fundraising", ex)
                null
            }
        }

    private fun findStore(member: Member): Store? =
        member.storeId?.let {
            try {
                marketplaceManagerApi.getStore(member.storeId!!).store
            } catch (ex: Exception) {
                LOGGER.warn("Unable to resolve Fundraising", ex)
                null
            }
        }
}
