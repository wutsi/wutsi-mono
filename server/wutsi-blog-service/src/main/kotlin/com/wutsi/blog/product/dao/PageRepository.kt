package com.wutsi.blog.product.dao

import com.wutsi.blog.product.domain.BookEntity
import com.wutsi.blog.product.domain.ProductEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface BookRepository : CrudRepository<BookEntity, Long> {
    fun findByProduct(product: ProductEntity): List<BookEntity>
}
