package com.wutsi.blog.app.page.store

import com.wutsi.blog.SortOrder
import com.wutsi.blog.app.backend.TrackingBackend
import com.wutsi.blog.app.form.TrackForm
import com.wutsi.blog.app.model.ProductModel
import com.wutsi.blog.app.model.UserModel
import com.wutsi.blog.app.page.AbstractStoreController
import com.wutsi.blog.app.service.CountryService
import com.wutsi.blog.app.service.ImageType
import com.wutsi.blog.app.service.OpenGraphImageGenerator
import com.wutsi.blog.app.service.ProductService
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.UserService
import com.wutsi.blog.app.util.CookieHelper
import com.wutsi.blog.app.util.CookieName
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.app.util.WhatsappUtil
import com.wutsi.blog.country.dto.Country
import com.wutsi.blog.product.dto.ProductSortStrategy
import com.wutsi.blog.product.dto.ProductStatus
import com.wutsi.blog.product.dto.ProductType
import com.wutsi.blog.product.dto.SearchProductContext
import com.wutsi.blog.product.dto.SearchProductRequest
import com.wutsi.platform.core.image.Dimension
import com.wutsi.platform.core.image.ImageService
import com.wutsi.platform.core.image.Transformation
import com.wutsi.platform.core.messaging.UrlShortener
import com.wutsi.tracking.manager.dto.PushTrackRequest
import org.slf4j.LoggerFactory
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.core.io.InputStreamResource
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.client.HttpClientErrorException
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

@Controller
@RequestMapping
class ProductController(
    private val productService: ProductService,
    private val userService: UserService,
    private val trackingBackend: TrackingBackend,
    private val tracingContext: RequestContext,
    private val urlShortener: UrlShortener,
    private val countryService: CountryService,
    private val opengraph: OpenGraphImageGenerator,
    private val imageService: ImageService,
    requestContext: RequestContext,
) : AbstractStoreController(requestContext) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(ProductController::class.java)
    }

    override fun pageName() = PageName.PRODUCT

    override fun shouldShowGoogleOneTap() = true

    override fun shouldBeIndexedByBots() = true

    @GetMapping("/product/{id}")
    fun index(
        @PathVariable id: Long,
        @RequestParam(required = false) referer: String? = null,
        @RequestParam(required = false, name = "ads-id") adsId: String? = null,
        model: Model
    ): String =
        index(id, title = "", referer, adsId, model)

    @GetMapping("/product/{id}/{title}")
    fun index(
        @PathVariable id: Long,
        @PathVariable title: String,
        @RequestParam(required = false) referer: String? = null,
        @RequestParam(required = false, name = "ads-id") adsId: String? = null,
        model: Model
    ): String {
        try {
            val product = productService.get(id)
            val store = storeService.get(product.storeId)
            val blog = userService.get(store.userId)
            val wallet = blog.walletId?.let { walletService.get(blog.walletId) }

            model.addAttribute("blog", blog)
            model.addAttribute("product", product)
            model.addAttribute("store", store)
            model.addAttribute("wallet", wallet)

            if (isPublished(product)) {
                model.addAttribute("page", toPage(product, blog))
                model.addAttribute("paymentProviderTypes", countryService.paymentProviderTypes)

                loadOtherProducts(product, model)
                loadWhatsappUrl(blog, product, model)
                loadDiscountBanner(product, blog, model)
                storeReferer(referer)
                storeCampaign(adsId)
            } else {
                return notFound(model, product)
            }
            return "store/product"
        } catch (ex: HttpClientErrorException) {
            LOGGER.warn("Unable to resolve the product", ex)
            return notFound(model, null)
        }
    }

    private fun isPublished(product: ProductModel): Boolean =
        product.status == ProductStatus.PUBLISHED && product.available

    private fun notFound(model: Model, product: ProductModel?): String {
        // Pages
        val page = createPage(
            name = PageName.PRODUCT_NOT_FOUND,
            title = requestContext.getMessage("page.home.metadata.title"),
            description = requestContext.getMessage("page.home.metadata.description"),
            robots = "noindex,nofollow",
        )
        model.addAttribute("page", page)

        // product recommendation
        val products = productService.search(
            SearchProductRequest(
                excludeProductIds = product?.let { prod -> listOf(prod.id) } ?: emptyList(),
                available = true,
                status = ProductStatus.PUBLISHED,
                sortBy = ProductSortStrategy.RECOMMENDED,
                sortOrder = SortOrder.DESCENDING,
                limit = 20,
                bubbleDownPurchasedProduct = true,
                searchContext = SearchProductContext(
                    userId = requestContext.currentUser()?.id,
                )
            )
        )
        if (products.isNotEmpty()) {
            model.addAttribute("products", products)
        }

        return "store/product_not_found"
    }

    @ResponseBody
    @PostMapping("/product/{id}/track")
    fun track(@PathVariable id: Long, @RequestBody form: TrackForm): Map<String, String> {
        val user = requestContext.currentUser()
        if (user?.superUser == true || productService.get(id).id == user?.id) {
            return emptyMap()
        }

        // Track
        trackingBackend.push(
            PushTrackRequest(
                time = form.time,
                correlationId = form.hitId,
                productId = id.toString(),
                event = form.event,
                deviceId = tracingContext.deviceId(),
                url = form.url,
                ua = form.ua,
                value = form.value,
                page = PageName.PRODUCT,
                referrer = form.referrer,
                accountId = user?.id?.toString(),
                ip = requestContext.remoteIp(),
                businessId = user?.storeId,
            ),
        )

        return emptyMap()
    }

    @GetMapping("/product/{id}/image.png")
    fun image(@PathVariable id: Long): ResponseEntity<InputStreamResource> {
        val product = productService.get(id)

        val out = ByteArrayOutputStream()
        opengraph.generate(
            type = ImageType.EBOOK,
            pictureUrl = product.originalImageUrl?.let { pictureUrl ->
                imageService.transform(
                    url = pictureUrl,
                    transformation = Transformation(
                        Dimension(
                            width = OpenGraphImageGenerator.EBOOK_IMAGE_WIDTH,
                            height = OpenGraphImageGenerator.EBOOK_IMAGE_HEIGHT,
                        ),
                    ),
                )
            },
            title = product.title,
            description = product.description,
            language = "en",
            output = out,
        )

        val input = ByteArrayInputStream(out.toByteArray())
        return ResponseEntity.ok()
            .contentType(MediaType.IMAGE_PNG)
            .body(InputStreamResource(input))
    }

    private fun storeReferer(referer: String?) {
        if (referer.isNullOrEmpty()) {
            CookieHelper.remove(CookieName.REFERER, requestContext.response)
        } else {
            CookieHelper.put(CookieName.REFERER, referer, requestContext.request, requestContext.response)
        }
    }

    private fun storeCampaign(campaign: String?) {
        if (campaign.isNullOrEmpty()) {
            CookieHelper.remove(CookieName.CAMPAIGN, requestContext.response)
        } else {
            CookieHelper.put(CookieName.CAMPAIGN, campaign, requestContext.request, requestContext.response)
        }
    }

    private fun toPage(product: ProductModel, blog: UserModel) = createPage(
        description = product.description ?: "",
        title = product.title,
        url = product.url,
        imageUrl = if (product.type == ProductType.EBOOK || product.type == ProductType.COMICS) {
            "$baseUrl/product/${product.id}/image.png"
        } else {
            product.imageUrl
        },
        type = if (product.type == ProductType.EBOOK) "book" else "website",
        author = blog.fullName,
        tags = product.category
            ?.longTitle
            ?.split(">")
            ?.map { category -> category.trim() }
            ?.toList()
            ?: emptyList()
    )

    private fun loadOtherProducts(product: ProductModel, model: Model) {
        val request = SearchProductRequest(
            storeIds = listOf(product.storeId),
            excludeProductIds = listOf(product.id),
            available = true,
            status = ProductStatus.PUBLISHED,
            sortBy = ProductSortStrategy.RECOMMENDED,
            sortOrder = SortOrder.DESCENDING,
            limit = 20,
            excludePurchasedProduct = true,
            searchContext = SearchProductContext(
                userId = requestContext.currentUser()?.id,
            )
        )

        try {
            // Hashtag products
            if (!product.hashtag.isNullOrEmpty()) {
                val products = productService.search(
                    request.copy(
                        hashtag = product.hashtag,
                        sortBy = ProductSortStrategy.TITLE,
                        sortOrder = SortOrder.ASCENDING,
                    )
                )
                if (products.isNotEmpty()) {
                    model.addAttribute("hashtagProducts", products)
                }
            }

            // Other products
            val products = productService.search(request.copy(excludeHashtag = product.hashtag))
            if (products.isNotEmpty()) {
                model.addAttribute("otherProducts", products)
            }
        } catch (ex: Exception) {
            LOGGER.warn("Unable to load other products", ex)
        }
    }

    private fun loadWhatsappUrl(blog: UserModel, product: ProductModel, model: Model) {
        if (blog.whatsappId != null) {
            val productUrl = urlShortener.shorten(baseUrl + product.slug)
            val message = requestContext.getMessage(
                "page.product.whatstapp",
                "",
                emptyArray(),
                LocaleContextHolder.getLocale()
            )
            val whatsappUrl = WhatsappUtil.url(blog.whatsappId, message, productUrl)
            model.addAttribute("whatsappUrl", whatsappUrl)
        }
    }

    private fun loadDiscountBanner(product: ProductModel, blog: UserModel, model: Model) {
        val country = Country.all.find { it.code.equals(blog.country, true) }
        if (country != null) {
            val amount = country.defaultDonationAmounts[0]
            model.addAttribute("donationAmount", country.createMoneyFormat().format(amount))
        }
    }
}
