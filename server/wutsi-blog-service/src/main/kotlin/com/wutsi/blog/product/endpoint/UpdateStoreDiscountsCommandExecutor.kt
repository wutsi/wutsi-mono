package com.wutsi.blog.product.endpoint

import com.wutsi.blog.product.dto.UpdateStoreDiscountsCommand
import com.wutsi.blog.product.service.StoreService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class UpdateStoreDiscountsCommandExecutor(private val service: StoreService) {
    @PostMapping("/v1/stores/commands/update-discounts")
    fun execute(@Valid @RequestBody request: UpdateStoreDiscountsCommand) {
        service.updateDiscounts(request)
    }
}
