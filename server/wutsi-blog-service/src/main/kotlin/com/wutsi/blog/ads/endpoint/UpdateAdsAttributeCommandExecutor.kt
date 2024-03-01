package com.wutsi.blog.ads.endpoint

import com.wutsi.blog.ads.dto.UpdateAdsAttributeCommand
import com.wutsi.blog.ads.service.AdsService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class UpdateAdsAttributeCommandExecutor(
    private val service: AdsService,
) {
    @PostMapping("/v1/ads/commands/update-attribute")
    fun execute(@Valid @RequestBody command: UpdateAdsAttributeCommand) {
        service.updateAttribute(command)
    }
}
