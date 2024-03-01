package com.wutsi.blog.ads.endpoint

import com.wutsi.blog.ads.dto.CreateAdsCommand
import com.wutsi.blog.ads.dto.CreateAdsResponse
import com.wutsi.blog.ads.service.AdsService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class CreateAdsCommandExecutor(
    private val service: AdsService,
) {
    @PostMapping("/v1/ads/commands/create")
    fun execute(@Valid @RequestBody command: CreateAdsCommand) = CreateAdsResponse(
        adsId = service.create(command).id!!
    )
}
