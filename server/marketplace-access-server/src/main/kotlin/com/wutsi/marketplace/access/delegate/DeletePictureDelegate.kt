package com.wutsi.marketplace.access.delegate

import com.wutsi.marketplace.access.service.PictureService
import com.wutsi.marketplace.access.service.ProductService
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class DeletePictureDelegate(
    private val service: PictureService,
    private val productService: ProductService,
) {
    @Transactional
    fun invoke(id: Long) {
        val picture = service.delete(id)
        val product = picture.product
        if (product.thumbnail?.id == picture.id) {
            val pictures = product.pictures.filter { !it.isDeleted }
            if (pictures.isEmpty()) {
                productService.setThumbnail(product, null)
            } else {
                productService.setThumbnail(product, pictures[0])
            }
        }
    }
}
