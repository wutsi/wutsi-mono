package com.wutsi.application.web.endpoint

import com.wutsi.application.web.Page
import com.wutsi.application.web.model.PageModel
import com.wutsi.enums.ProductSort
import com.wutsi.marketplace.manager.dto.OfferSummary
import com.wutsi.marketplace.manager.dto.SearchOfferRequest
import com.wutsi.membership.manager.dto.Member
import org.springframework.context.MessageSource
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import java.util.Locale

@Controller
@RequestMapping
class ShopController(
    private val messages: MessageSource,
) : AbstractController() {
    @GetMapping("/@{name}/shop")
    fun index(@PathVariable name: String, model: Model): String =
        render(
            resolveCurrentMerchant(name),
            model,
        )

    @GetMapping("/u/{id}/shop")
    fun index(@PathVariable id: Long, model: Model): String =
        render(
            resolveCurrentMerchant(id),
            model,
        )

    private fun render(merchant: Member, model: Model): String {
        val country = regulationEngine.country(merchant.country)
        val memberModel = mapper.toMemberModel(merchant)
        val offers = findOffers(merchant)

        model.addAttribute("page", createPage(merchant))
        model.addAttribute("member", memberModel)
        model.addAttribute(
            "offers",
            offers.map {
                mapper.toOfferModel(it, country, merchant)
            },
        )

        return "shop"
    }

    private fun createPage(merchant: Member) = PageModel(
        name = Page.SHOP,
        title = merchant.displayName + " - " + messages.getMessage("tab.shop", emptyArray(), Locale(merchant.language)),
        robots = "noindex",
    )

    private fun findOffers(member: Member): List<OfferSummary> {
        if (member.storeId == null) {
            return emptyList()
        }

        return marketplaceManagerApi.searchOffer(
            request = SearchOfferRequest(
                storeId = member.storeId,
                limit = regulationEngine.maxProducts(),
                sortBy = ProductSort.RECOMMENDED.name,
            ),
        ).offers
    }
}
