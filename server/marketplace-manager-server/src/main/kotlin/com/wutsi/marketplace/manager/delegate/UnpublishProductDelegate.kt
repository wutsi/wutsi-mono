package com.wutsi.marketplace.manager.delegate

import com.wutsi.checkout.manager.util.SecurityUtil
import com.wutsi.enums.ProductStatus
import com.wutsi.marketplace.access.dto.UpdateProductStatusRequest
import org.springframework.stereotype.Service

@Service
public class UnpublishProductDelegate : AbstractUpdateProductDelegate() {
    public fun invoke(id: Long) {
        val product = findProduct(id)
        val account = findAccount(SecurityUtil.getAccountId())
        validate(product, account)
        unpublish(id)
    }

    fun unpublish(id: Long) {
        marketplaceAccessApi.updateProductStatus(
            id = id,
            request = UpdateProductStatusRequest(
                status = ProductStatus.DRAFT.name,
            ),
        )
    }
}
