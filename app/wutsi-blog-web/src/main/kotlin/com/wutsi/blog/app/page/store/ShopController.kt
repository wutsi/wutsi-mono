package com.wutsi.blog.app.page.store

import com.wutsi.blog.app.model.UserModel
import com.wutsi.blog.app.page.AbstractStoreController
import com.wutsi.blog.app.service.ImageType
import com.wutsi.blog.app.service.OpenGraphImageGenerator
import com.wutsi.blog.app.service.ProductService
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.UserService
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.product.dto.ProductStatus
import com.wutsi.blog.product.dto.SearchProductRequest
import com.wutsi.platform.core.image.Dimension
import com.wutsi.platform.core.image.ImageService
import com.wutsi.platform.core.image.Transformation
import org.springframework.core.io.InputStreamResource
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

@Controller
@RequestMapping
class ShopController(
    private val productService: ProductService,
    private val userService: UserService,
    private val opengraph: OpenGraphImageGenerator,
    private val imageService: ImageService,
    requestContext: RequestContext,
) : AbstractStoreController(requestContext) {
    companion object {
        const val LIMIT = 20
    }

    override fun pageName() = PageName.SHOP

    override fun shouldBeIndexedByBots() = true

    override fun shouldShowGoogleOneTap() = true

    @GetMapping("/@/{name}/shop")
    fun index(@PathVariable name: String, model: Model): String {
        val blog = userService.get(name)
        val store = checkStoreAccess(blog)

        model.addAttribute("blog", blog)
        model.addAttribute("page", getPage(blog))

        val products = productService.search(
            SearchProductRequest(
                storeIds = listOf(store.id),
                limit = LIMIT,
                status = ProductStatus.PUBLISHED,
                available = true,
            )
        )
        if (products.isNotEmpty()) {
            model.addAttribute("products", products)
        }
        return "store/shop"
    }

    @GetMapping("/@/{name}/shop.png")
    fun image(@PathVariable name: String): ResponseEntity<InputStreamResource> {
        val blog = userService.get(name)
        if (!blog.blog || blog.storeId.isNullOrEmpty()) {
            return ResponseEntity.notFound().build()
        }

        val out = ByteArrayOutputStream()
        opengraph.generate(
            type = ImageType.SHOP,
            pictureUrl = blog.pictureUrl?.let { pictureUrl ->
                imageService.transform(
                    url = pictureUrl,
                    transformation = Transformation(
                        Dimension(
                            OpenGraphImageGenerator.IMAGE_WIDTH,
                            OpenGraphImageGenerator.IMAGE_HEIGHT,
                        ),
                    ),
                )
            },
            title = blog.fullName,
            description = blog.biography,
            language = blog.language,
            output = out,
        )

        val input = ByteArrayInputStream(out.toByteArray())
        return ResponseEntity.ok()
            .contentType(MediaType.IMAGE_PNG)
            .body(InputStreamResource(input))
    }

    private fun getPage(user: UserModel) = createPage(
        description = "",
        title = requestContext.getMessage("page.shop.metadata.title") + " | ${user.fullName}",
        url = url(user) + "/shop",
        imageUrl = "$baseUrl/@/${user.name}/shop.png",
    )
}
