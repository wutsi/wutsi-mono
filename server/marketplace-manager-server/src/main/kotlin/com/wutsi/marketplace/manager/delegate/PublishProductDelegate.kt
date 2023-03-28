package com.wutsi.marketplace.manager.delegate

import com.wutsi.checkout.manager.util.SecurityUtil
import com.wutsi.enums.ProductStatus
import com.wutsi.marketplace.access.dto.UpdateProductStatusRequest
import org.springframework.stereotype.Service

@Service
public class PublishProductDelegate : AbstractUpdateProductDelegate() {
    public fun invoke(id: Long) {
        val product = findProduct(id)
        val account = findAccount(SecurityUtil.getAccountId())
        validate(product, account, true)
        publish(id)
    }

    private fun publish(productId: Long) {
        marketplaceAccessApi.updateProductStatus(
            id = productId,
            request = UpdateProductStatusRequest(
                status = ProductStatus.PUBLISHED.name,
            ),
        )
    }
}
