package com.wutsi.blog.app.service

import com.wutsi.blog.app.backend.AdsBackend
import com.wutsi.blog.app.mapper.AdsMapper
import org.springframework.stereotype.Component

@Component
class AdsService(
    private val backend: AdsBackend,
    private val mapper: AdsMapper,
) {

}
