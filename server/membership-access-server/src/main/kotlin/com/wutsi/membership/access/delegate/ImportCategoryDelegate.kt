package com.wutsi.membership.access.delegate

import com.wutsi.membership.access.service.CategoryService
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
public class ImportCategoryDelegate(private val service: CategoryService) {
    @Transactional
    public fun invoke(language: String) {
        service.import(language)
    }
}
