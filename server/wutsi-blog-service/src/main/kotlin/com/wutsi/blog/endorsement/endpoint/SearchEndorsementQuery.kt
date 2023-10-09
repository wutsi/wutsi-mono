package com.wutsi.blog.endorsement.endpoint

import com.wutsi.blog.endorsement.dto.Endorsement
import com.wutsi.blog.endorsement.dto.SearchEndorsementRequest
import com.wutsi.blog.endorsement.dto.SearchEndorsementResponse
import com.wutsi.blog.endorsement.service.EndorsementService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/v1/endorsements/queries/search")
class SearchEndorsementQuery(
    private val service: EndorsementService,
) {
    @PostMapping
    fun search(@Valid @RequestBody request: SearchEndorsementRequest): SearchEndorsementResponse =
        SearchEndorsementResponse(
            endorsements = service.search(request).map {
                Endorsement(
                    userId = it.userId,
                    endorserId = it.endorserId,
                    blurb = it.blurb,
                    creationDateTime = it.creationDateTime,
                )
            },
        )
}
