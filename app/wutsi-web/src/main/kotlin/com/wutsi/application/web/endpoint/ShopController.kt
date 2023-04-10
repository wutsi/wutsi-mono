package com.wutsi.application.web.endpoint

import com.wutsi.application.web.Page
import com.wutsi.application.web.model.MemberModel
import com.wutsi.application.web.model.OfferModel
import com.wutsi.application.web.model.PageModel
import com.wutsi.enums.ProductSort
import com.wutsi.error.ErrorURN
import com.wutsi.marketplace.manager.dto.SearchOfferRequest
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.exception.NotFoundException
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

    private fun render(merchant: MemberModel, model: Model): String {
        merchant.store ?: throw NotFoundException(
            error = Error(
                code = ErrorURN.FUNDRAISING_NOT_FOUND.urn,
            ),
        )

        val offers = findOffers(merchant)

        model.addAttribute("page", createPage(merchant))
        model.addAttribute("member", merchant)
        model.addAttribute("offers", offers)

        return "shop"
    }

    private fun createPage(merchant: MemberModel) = PageModel(
        name = Page.SHOP,
        title = merchant.displayName + " - " + messages.getMessage("tab.shop", emptyArray(), Locale(merchant.language)),
        robots = "noindex",
    )

    private fun findOffers(merchant: MemberModel): List<OfferModel> {
        if (merchant.storeId == null) {
            return emptyList()
        }

        return marketplaceManagerApi.searchOffer(
            request = SearchOfferRequest(
                storeId = merchant.storeId,
                limit = regulationEngine.maxProducts(),
                sortBy = ProductSort.RECOMMENDED.name,
            ),
        ).offers.map {
            mapper.toOfferModel(it, merchant)
        }
    }
}
