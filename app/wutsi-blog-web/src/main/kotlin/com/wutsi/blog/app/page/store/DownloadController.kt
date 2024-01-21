package com.wutsi.blog.app.page.store

import com.wutsi.blog.app.model.ProductModel
import com.wutsi.blog.app.model.TransactionModel
import com.wutsi.blog.app.service.BookService
import com.wutsi.blog.app.service.ProductService
import com.wutsi.blog.app.service.TransactionService
import com.wutsi.blog.error.ErrorCode
import com.wutsi.blog.product.dto.SearchBookRequest
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.Parameter
import com.wutsi.platform.core.error.exception.NotFoundException
import com.wutsi.platform.core.storage.StorageService
import com.wutsi.platform.payment.core.Status
import feign.FeignException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import java.net.URL

@Controller
class DownloadController(
    private val transactionService: TransactionService,
    private val storage: StorageService,
    private val bookService: BookService,
    private val productService: ProductService,
    @Value("\${wutsi.application.server-url}") private val baseUrl: String,
) {
    @GetMapping("/product/{productId}/download/{transactionId}")
    fun index(
        @PathVariable productId: Long,
        @PathVariable transactionId: String,
        request: HttpServletRequest,
        response: HttpServletResponse,
    ) {
        val tx = findTransaction(transactionId)
        if (productId != tx.product?.id) {
            throw NotFoundException(
                error = Error(
                    code = ErrorCode.PRODUCT_NOT_FOUND,
                    message = "Invalid productId"
                )
            )
        }

        val product = tx.product
        if (product.streamable) {
            val book = bookService.search(
                SearchBookRequest(transactionId = tx.id, limit = 1)
            ).firstOrNull()
            if (book == null) {
                response.sendError(404)
            } else {
                response.sendRedirect("$baseUrl/me/play/${book.id}")
            }
        } else {
            val filename = toFilename(product)
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"$filename\"")
            response.setHeader(HttpHeaders.CONTENT_TYPE, product.fileContentType)
            response.setContentLength(product.fileContentLength.toInt())
            storage.get(URL(product.fileUrl), response.outputStream)
        }
    }

    private fun toFilename(product: ProductModel): String {
        val i = product.slug.lastIndexOf("/")
        return product.slug.substring(i + 1) + "." + product.fileExtension
    }

    private fun findTransaction(transactionId: String): TransactionModel {
        try {
            val tx = transactionService.get(transactionId, false)
            if (tx.status != Status.SUCCESSFUL) {
                throw NotFoundException(
                    error = Error(
                        code = ErrorCode.TRANSACTION_NOT_FOUND,
                        message = "Transaction not successful",
                        parameter = Parameter(
                            name = "transactionId",
                            value = transactionId
                        )
                    )
                )
            }
            return tx
        } catch (ex: FeignException.NotFound) {
            throw NotFoundException(
                error = Error(
                    code = ErrorCode.TRANSACTION_NOT_FOUND,
                    parameter = Parameter(
                        name = "transactionId",
                        value = transactionId
                    )
                ),
                ex
            )
        }
    }
}
