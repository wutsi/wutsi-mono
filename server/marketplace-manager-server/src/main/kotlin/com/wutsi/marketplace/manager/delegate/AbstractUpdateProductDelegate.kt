package com.wutsi.marketplace.manager.delegate

import com.wutsi.enums.ProductStatus
import com.wutsi.marketplace.access.MarketplaceAccessApi
import com.wutsi.marketplace.access.dto.Product
import com.wutsi.membership.access.MembershipAccessApi
import com.wutsi.membership.access.dto.Account
import com.wutsi.regulation.Rule
import com.wutsi.regulation.RuleSet
import com.wutsi.regulation.rule.AccountShouldBeActiveRule
import com.wutsi.regulation.rule.AccountShouldBeBusinessRule
import com.wutsi.regulation.rule.AccountShouldBeOwnerOfProductRule
import com.wutsi.regulation.rule.AccountShouldHaveStoreRule
import com.wutsi.regulation.rule.ProductDigitalDownloadShouldHaveFileRule
import com.wutsi.regulation.rule.ProductEventMeetingIdShouldBeValidRule
import com.wutsi.regulation.rule.ProductEventShouldHaveEndDateRule
import com.wutsi.regulation.rule.ProductEventShouldHaveMeetingIdRule
import com.wutsi.regulation.rule.ProductEventShouldHaveStartDateBeforeEndDateRule
import com.wutsi.regulation.rule.ProductEventShouldHaveStartDateInFutureRule
import com.wutsi.regulation.rule.ProductEventShouldHaveStartDateRule
import com.wutsi.regulation.rule.ProductShouldHavePictureRule
import com.wutsi.regulation.rule.ProductShouldHavePriceRule
import com.wutsi.regulation.rule.ProductShouldHaveStockRule
import org.springframework.beans.factory.annotation.Autowired

abstract class AbstractUpdateProductDelegate {
    @Autowired
    protected lateinit var marketplaceAccessApi: MarketplaceAccessApi

    @Autowired
    protected lateinit var membershipAccessApi: MembershipAccessApi

    protected open fun validate(product: Product, account: Account, includePublishRules: Boolean = false) {
        val rules = mutableListOf<Rule>()
        if (includePublishRules || product.status == ProductStatus.PUBLISHED.name) {
            rules.addAll(
                listOf(
                    ProductShouldHavePictureRule(product),
                    ProductShouldHaveStockRule(product),
                    ProductEventShouldHaveMeetingIdRule(product),
                    ProductEventShouldHaveStartDateRule(product),
                    ProductEventShouldHaveEndDateRule(product),
                    ProductEventShouldHaveStartDateBeforeEndDateRule(product),
                    ProductEventShouldHaveStartDateInFutureRule(product),
                    ProductDigitalDownloadShouldHaveFileRule(product),
                    ProductShouldHavePriceRule(product),
                    ProductEventMeetingIdShouldBeValidRule(product),
                ),
            )
        }
        rules.addAll(
            listOf(
                AccountShouldBeBusinessRule(account),
                AccountShouldBeActiveRule(account),
                AccountShouldHaveStoreRule(account),
                AccountShouldBeOwnerOfProductRule(account, product),
            ),
        )
        RuleSet(rules).check()
    }

    protected fun findProduct(productId: Long): Product =
        marketplaceAccessApi.getProduct(productId).product

    protected fun findAccount(accountId: Long): Account =
        membershipAccessApi.getAccount(accountId).account
}
