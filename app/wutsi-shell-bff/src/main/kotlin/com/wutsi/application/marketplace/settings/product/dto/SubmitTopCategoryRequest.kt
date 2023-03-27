package com.wutsi.application.marketplace.settings.product.dto

import javax.validation.constraints.NotEmpty

data class SubmitTopCategoryRequest(
    @NotEmpty val categoryId: Long = -1,
)
