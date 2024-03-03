package com.wutsi.blog.ads.endpoint

import com.wutsi.blog.ads.dto.PublishAdsCommand
import com.wutsi.blog.ads.service.AdsService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class PublishAdsCommandExecutor(
    private val service: AdsService,
) {
    @PostMapping("/v1/ads/commands/publish")
    fun execute(@Valid @RequestBody command: PublishAdsCommand) {
        service.publish(command)
    }
}
