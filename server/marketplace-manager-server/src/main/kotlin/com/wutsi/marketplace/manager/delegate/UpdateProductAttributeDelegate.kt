package com.wutsi.marketplace.manager.delegate

import com.wutsi.checkout.manager.util.SecurityUtil
import com.wutsi.marketplace.manager.dto.UpdateProductAttributeListRequest
import com.wutsi.platform.core.logging.KVLogger
import org.springframework.stereotype.Service

@Service
public class UpdateProductAttributeDelegate(
    private val logger: KVLogger,
) : AbstractUpdateProductDelegate() {
    public fun invoke(request: UpdateProductAttributeListRequest) {
        logger.add("request_product_id", request.productId)
        request.attributes.forEach {
            logger.add("request_attribute_${it.name}", it.value)
        }

        val product = findProduct(request.productId)
        val account = findAccount(SecurityUtil.getAccountId())
        validate(product, account)
        update(request)
    }

    private fun update(request: UpdateProductAttributeListRequest) {
        request.attributes.forEach {
            marketplaceAccessApi.updateProductAttribute(
                id = request.productId,
                request = com.wutsi.marketplace.access.dto.UpdateProductAttributeRequest(
                    name = it.name,
                    value = it.value,
                ),
            )
        }
    }
}
