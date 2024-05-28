package com.wutsi.blog.app.service

import com.wutsi.blog.app.mapper.CountryMapper
import com.wutsi.blog.app.model.ProductModel
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class LiretamaServiceTest {
    private val affiliateId = "123456"
    private val service = LiretamaService(affiliateId, CountryMapper("https://assets.wutsi.com"))
    private val liretamaUrl = "https://www.liretama.com/livres/la-cousine-de-mon-pere"

    @Test
    fun `add affiliate-id`() {
        val product = ProductModel(liretamaUrl = liretamaUrl)
        val xurl = service.toUrl(product)

        assertEquals("$liretamaUrl?pid=$affiliateId", xurl)
    }

    @Test
    fun `replace affiliate-id`() {
        val product = ProductModel(liretamaUrl = "$liretamaUrl?pid=4304309")
        val xurl = service.toUrl(product)

        assertEquals("$liretamaUrl?pid=$affiliateId", xurl)
    }

    @Test
    fun `no liretama-url`() {
        val product = ProductModel(id = 111)
        val xurl = service.toUrl(product)

        assertEquals("/product/${product.id}", xurl)
    }
}