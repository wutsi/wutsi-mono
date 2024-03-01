package com.wutsi.blog.ads.endpoint

import com.wutsi.blog.ads.dto.StartAdsCommand
import com.wutsi.blog.ads.service.AdsService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class StartAdsCommandExecutor(
    private val service: AdsService,
) {
    @PostMapping("/v1/ads/commands/start")
    fun execute(@Valid @RequestBody command: StartAdsCommand) {
        service.start(command)
    }
}
