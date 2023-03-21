package com.wutsi.membership.access.delegate

import com.wutsi.membership.access.service.PlaceService
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
public class ImportPlaceDelegate(private val service: PlaceService) {
    @Transactional
    public fun invoke(country: String) {
        service.import(country)
    }
}
