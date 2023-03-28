package com.wutsi.marketplace.manager.delegate

import com.wutsi.enums.ProductStatus
import com.wutsi.marketplace.access.MarketplaceAccessApi
import com.wutsi.marketplace.access.dto.Product
import com.wutsi.membership.access.MembershipAccessApi
import com.wutsi.membership.access.dto.Account
import com.wutsi.workflow.Rule
import com.wutsi.workflow.RuleSet
import com.wutsi.workflow.rule.account.AccountShouldBeActiveRule
import com.wutsi.workflow.rule.account.AccountShouldBeBusinessRule
import com.wutsi.workflow.rule.account.AccountShouldBeOwnerOfProductRule
import com.wutsi.workflow.rule.account.AccountShouldHaveStoreRule
import com.wutsi.workflow.rule.account.ProductDigitalDownloadShouldHaveFileRule
import com.wutsi.workflow.rule.account.ProductEventMeetingIdShouldBeValidRule
import com.wutsi.workflow.rule.account.ProductEventShouldHaveEndDateRule
import com.wutsi.workflow.rule.account.ProductEventShouldHaveMeetingIdRule
import com.wutsi.workflow.rule.account.ProductEventShouldHaveStartDateBeforeEndDateRule
import com.wutsi.workflow.rule.account.ProductEventShouldHaveStartDateInFutureRule
import com.wutsi.workflow.rule.account.ProductEventShouldHaveStartDateRule
import com.wutsi.workflow.rule.account.ProductShouldHavePictureRule
import com.wutsi.workflow.rule.account.ProductShouldHavePriceRule
import com.wutsi.workflow.rule.account.ProductShouldHaveStockRule
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
