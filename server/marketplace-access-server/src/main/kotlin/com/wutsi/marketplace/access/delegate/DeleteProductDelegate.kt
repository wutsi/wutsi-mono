package com.wutsi.marketplace.access.delegate

import com.wutsi.marketplace.access.service.ProductService
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class DeleteProductDelegate(private val service: ProductService) {
    @Transactional
    fun invoke(id: Long) {
        service.delete(id)
    }
}
