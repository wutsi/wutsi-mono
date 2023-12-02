package com.wutsi.blog.product.endpoint

import com.wutsi.blog.product.dto.CreateStoreCommand
import com.wutsi.blog.product.dto.CreateStoreResponse
import com.wutsi.blog.product.service.StoreService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class CreateStoreCommandExecutor(
    private val service: StoreService,
) {
    @PostMapping("/v1/stores")
    fun execute(@Valid @RequestBody request: CreateStoreCommand): CreateStoreResponse =
        CreateStoreResponse(
            storeId = service.create(request).id!!
        )
}
