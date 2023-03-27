package com.wutsi.application.marketplace.product.screen

import com.wutsi.application.Page
import com.wutsi.application.Theme
import com.wutsi.application.common.endpoint.AbstractSecuredEndpoint
import com.wutsi.application.util.SecurityUtil
import com.wutsi.application.widget.GridWidget
import com.wutsi.application.widget.OfferWidget
import com.wutsi.enums.ProductSort
import com.wutsi.flutter.sdui.Button
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Divider
import com.wutsi.flutter.sdui.SingleChildScrollView
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.Widget
import com.wutsi.marketplace.manager.MarketplaceManagerApi
import com.wutsi.marketplace.manager.dto.SearchOfferRequest
import com.wutsi.platform.core.image.ImageService
import com.wutsi.regulation.RegulationEngine
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/products/2/list/fragment")
class ProductV2ListFragment(
    private val marketplaceManagerApi: MarketplaceManagerApi,
    private val regulationEngine: RegulationEngine,
    private val imageService: ImageService,
) : AbstractSecuredEndpoint() {
    @PostMapping
    fun widget(@RequestParam(required = false) id: Long? = null): Widget {
        val member = membershipManagerApi.getMember(
            id ?: SecurityUtil.getMemberId(),
        ).member
        val country = regulationEngine.country(member.country)
        if (!member.business || member.storeId == null || !country.supportsStore) {
            return Container().toWidget()
        }

        val offers = marketplaceManagerApi.searchOffer(
            request = SearchOfferRequest(
                storeId = member.storeId,
                limit = regulationEngine.maxProducts(),
                sortBy = ProductSort.RECOMMENDED.name,
            ),
        ).offers

        return SingleChildScrollView(
            child = Column(
                children = listOfNotNull(
                    Container(
                        padding = 10.0,
                        child = Text(
                            caption = if (offers.isEmpty()) {
                                getText("page.product.list.0_product")
                            } else if (offers.size == 1) {
                                getText("page.product.list.1_product")
                            } else {
                                getText("page.product.list.n_products", arrayOf(offers.size))
                            },
                        ),
                    ),
                    Divider(color = Theme.COLOR_DIVIDER, height = 1.0),

                    if (id == SecurityUtil.getMemberId() && offers.isEmpty()) {
                        Container(
                            padding = 10.0,
                            child = Button(
                                caption = getText("page.product.button.configure"),
                                action = gotoUrl(
                                    url = urlBuilder.build(Page.getSettingsStoreUrl()),
                                ),
                            ),
                        )
                    } else {
                        null
                    },

                    GridWidget(
                        columns = 2,
                        children = offers.map {
                            OfferWidget.of(
                                offer = it,
                                country = country,
                                action = gotoUrl(urlBuilder.build("${Page.getProductUrl()}?id=${it.product.id}")),
                                imageService = imageService,
                                timezoneId = member.timezoneId,
                                regulationEngine = regulationEngine,
                            )
                        },
                    ),
                ),
            ),
        ).toWidget()
    }
}
