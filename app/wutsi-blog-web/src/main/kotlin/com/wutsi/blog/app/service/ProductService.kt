package com.wutsi.blog.app.service

import com.wutsi.blog.app.backend.OfferBackend
import com.wutsi.blog.app.backend.ProductBackend
import com.wutsi.blog.app.form.CreateProductForm
import com.wutsi.blog.app.form.ProductAttributeForm
import com.wutsi.blog.app.mapper.ProductMapper
import com.wutsi.blog.app.model.ProductModel
import com.wutsi.blog.app.model.StoreModel
import com.wutsi.blog.product.dto.CreateProductCommand
import com.wutsi.blog.product.dto.ImportProductCommand
import com.wutsi.blog.product.dto.PublishProductCommand
import com.wutsi.blog.product.dto.SearchCategoryRequest
import com.wutsi.blog.product.dto.SearchOfferRequest
import com.wutsi.blog.product.dto.SearchProductRequest
import com.wutsi.blog.product.dto.UpdateProductAttributeCommand
import org.springframework.stereotype.Component

@Component
class ProductService(
    private val backend: ProductBackend,
    private val offerBackend: OfferBackend,
    private val mapper: ProductMapper,
    private val requestContext: RequestContext,
    private val categoryService: CategoryService,
) {
    fun import(cmd: ImportProductCommand) {
        backend.import(cmd)
    }

    fun create(store: StoreModel, form: CreateProductForm): Long =
        backend.create(
            CreateProductCommand(
                storeId = store.id,
                title = form.title,
                description = form.description,
                categoryId = form.categoryId!!,
                type = form.type,
                price = form.price,
                available = true,
            )
        ).productId

    fun updateAttribute(id: Long, form: ProductAttributeForm) {
        backend.updateAttribute(
            UpdateProductAttributeCommand(
                productId = id,
                name = form.name,
                value = form.value?.ifEmpty { null }
            )
        )
    }

    fun search(request: SearchProductRequest): List<ProductModel> {
        val products = backend.search(request).products
        if (products.isEmpty()) {
            return emptyList()
        }

        val offerMap = offerBackend.search(
            SearchOfferRequest(
                userId = requestContext.currentUser()?.id,
                productIds = products.map { product -> product.id }
            )
        ).offers.associateBy { it.productId }

        val categoryIds = products.mapNotNull { product -> product.categoryId }.toSet().toList()
        val categoryMap = if (categoryIds.isEmpty()) {
            emptyMap()
        } else {
            categoryService.search(
                SearchCategoryRequest(
                    categoryIds = categoryIds,
                    limit = categoryIds.size
                )
            ).associateBy { it.id }
        }

        return products.map { product ->
            mapper.toProductModel(
                product,
                offerMap[product.id],
                product.categoryId?.let { categoryMap[product.categoryId] }
            )
        }
    }

    fun get(id: Long): ProductModel {
        val product = backend.get(id).product
        val offers = offerBackend.search(
            SearchOfferRequest(
                userId = requestContext.currentUser()?.id,
                productIds = listOf(id)
            )
        ).offers

        return mapper.toProductModel(product, offers.firstOrNull())
    }

    fun publish(id: Long) {
        backend.publish(PublishProductCommand(id))
    }
}
