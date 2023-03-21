package com.wutsi.marketplace.access.delegate

import com.wutsi.marketplace.access.service.CategoryService
import org.springframework.stereotype.Service

@Service
public class ImportCategoryDelegate(private val service: CategoryService) {
    public fun invoke() {
        service.import()
    }
}
