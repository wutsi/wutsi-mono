package com.wutsi.membership.access.delegate

import com.wutsi.membership.access.dto.SavePlaceRequest
import com.wutsi.membership.access.service.PlaceService
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
public class SavePlaceDelegate(private val service: PlaceService) {
    @Transactional
    public fun invoke(request: SavePlaceRequest) {
        service.save(request)
    }
}
