package com.wutsi.application.web.endpoint

import com.wutsi.application.web.Page
import com.wutsi.application.web.model.MemberModel
import com.wutsi.application.web.model.PageModel
import com.wutsi.enums.ProductSort
import com.wutsi.marketplace.manager.dto.OfferSummary
import com.wutsi.marketplace.manager.dto.SearchOfferRequest
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

    private fun render(merchant: Member, model: Model): String {
        val country = regulationEngine.country(merchant.country)
        val memberModel = mapper.toMemberModel(merchant)
        val offers = findFeatureOffers(merchant)

        model.addAttribute("page", createPage(memberModel))
        model.addAttribute("member", memberModel)
        model.addAttribute(
            "offers",
            offers.map {
                mapper.toOfferModel(it, country, merchant)
            }.ifEmpty { null },
        )

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

    private fun findFeatureOffers(member: Member): List<OfferSummary> {
        if (member.storeId == null) {
            return emptyList()
        }

        try {
            return marketplaceManagerApi.searchOffer(
                request = SearchOfferRequest(
                    storeId = member.storeId,
                    limit = 4,
                    sortBy = ProductSort.RECOMMENDED.name,
                ),
            ).offers
        } catch (ex: Exception) {
            LOGGER.warn("Unable to resolve featured offers", ex)
            return emptyList()
        }
    }
}
