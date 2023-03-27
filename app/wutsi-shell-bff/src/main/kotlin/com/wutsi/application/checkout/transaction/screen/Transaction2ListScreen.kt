package com.wutsi.application.checkout.transaction.screen

import com.wutsi.application.Page
import com.wutsi.application.Theme
import com.wutsi.application.common.endpoint.AbstractSecuredEndpoint
import com.wutsi.application.util.SecurityUtil
import com.wutsi.application.widget.TransactionWidget
import com.wutsi.checkout.manager.CheckoutManagerApi
import com.wutsi.checkout.manager.dto.SearchTransactionRequest
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Divider
import com.wutsi.flutter.sdui.Flexible
import com.wutsi.flutter.sdui.ListView
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.enums.Alignment
import com.wutsi.regulation.RegulationEngine
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/transactions/2/list")
class Transaction2ListScreen(
    private val checkoutManagerApi: CheckoutManagerApi,
    private val regulationEngine: RegulationEngine,
) : AbstractSecuredEndpoint() {
    @PostMapping
    fun index(): Widget {
        val member = membershipManagerApi.getMember(SecurityUtil.getMemberId()).member
        val country = regulationEngine.country(member.country)
        val txs = checkoutManagerApi.searchTransaction(
            request = SearchTransactionRequest(
                businessId = member.businessId,
                limit = 100,
            ),
        ).transactions

        return Screen(
            id = Page.TRANSACTION_LIST,
            appBar = AppBar(
                elevation = 0.0,
                backgroundColor = Theme.COLOR_WHITE,
                foregroundColor = Theme.COLOR_BLACK,
                title = getText("page.transaction.list.app-bar.title"),
            ),
            bottomNavigationBar = createBottomNavigationBarWidget(member),
            child = Column(
                children = listOfNotNull(
                    Container(
                        padding = 10.0,
                        alignment = Alignment.Center,
                        child = Text(
                            caption = if (txs.isEmpty()) {
                                getText("page.transaction.list.count-0")
                            } else if (txs.size == 1) {
                                getText("page.transaction.list.count-1")
                            } else {
                                getText("page.transaction.list.count-n", arrayOf(txs.size))
                            },
                        ),
                    ),
                    Divider(height = 1.0, color = Theme.COLOR_DIVIDER),
                    Flexible(
                        child = ListView(
                            separatorColor = Theme.COLOR_DIVIDER,
                            separator = true,
                            children = txs.map {
                                TransactionWidget.of(
                                    tx = it,
                                    country = country,
                                    action = gotoUrl(
                                        url = urlBuilder.build(Page.getTransactionUrl()),
                                        parameters = mapOf(
                                            "id" to it.id,
                                        ),
                                    ),
                                    merchant = member.business,
                                    timezoneId = member.timezoneId,
                                )
                            },
                        ),
                    ),
                ),
            ),
        ).toWidget()
    }
}
