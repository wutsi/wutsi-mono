package com.wutsi.marketplace.manager.delegate

import com.wutsi.checkout.manager.util.SecurityUtil
import com.wutsi.marketplace.access.MarketplaceAccessApi
import com.wutsi.marketplace.access.dto.Store
import com.wutsi.marketplace.manager.dto.CreateProductRequest
import com.wutsi.marketplace.manager.dto.CreateProductResponse
import com.wutsi.membership.access.MembershipAccessApi
import com.wutsi.membership.access.dto.Account
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.regulation.RegulationEngine
import com.wutsi.regulation.RuleSet
import com.wutsi.regulation.rule.AccountShouldBeActiveRule
import com.wutsi.regulation.rule.AccountShouldBeBusinessRule
import com.wutsi.regulation.rule.AccountShouldHaveStoreRule
import com.wutsi.regulation.rule.StoreShouldNotHaveTooManyProductsRule
import org.springframework.stereotype.Service

@Service
class CreateProductDelegate(
    private val marketplaceAccessApi: MarketplaceAccessApi,
    private val membershipAccessApi: MembershipAccessApi,
    private val regulationEngine: RegulationEngine,
    private val logger: KVLogger,
) {
    fun invoke(request: CreateProductRequest): CreateProductResponse {
        logger.add("request_picture_url", request.pictureUrl)
        logger.add("request_title", request.title)
        logger.add("request_summary", request.summary)
        logger.add("request_price", request.price)
        logger.add("request_category_id", request.categoryId)

        val account = findAccount(SecurityUtil.getAccountId())
        val store = account.storeId?.let { findStore(it) }
        validate(account, store)
        val productId = create(account, request)

        return CreateProductResponse(
            productId = productId,
        )
    }

    protected fun validate(account: Account, store: Store?) =
        RuleSet(
            listOfNotNull(
                AccountShouldBeActiveRule(account),
                AccountShouldBeBusinessRule(account),
                AccountShouldHaveStoreRule(account),
                store?.let { StoreShouldNotHaveTooManyProductsRule(it, regulationEngine) },
            ),
        ).check()

    private fun create(account: Account, request: CreateProductRequest): Long =
        marketplaceAccessApi.createProduct(
            request = com.wutsi.marketplace.access.dto.CreateProductRequest(
                storeId = account.storeId ?: -1,
                pictureUrl = request.pictureUrl,
                title = request.title,
                summary = request.summary,
                price = request.price,
                categoryId = request.categoryId,
                quantity = request.quantity,
                type = request.type,
            ),
        ).productId

    private fun findAccount(id: Long) =
        membershipAccessApi.getAccount(id).account

    private fun findStore(id: Long) =
        marketplaceAccessApi.getStore(id).store
}
