package com.wutsi.blog.channel.service

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ChannelRegistryTest {
    @Autowired
    private lateinit var set: ChannelRegistry

    @Test
    fun validate() {
        // Channel removed from registry:
        //  - telegram - REMOVED
        //  - twitter - REMOVED
        //  - firebase - REMOVED
        //  - linkedin - REMOVED
        //  - facebook - REMOVED
        assertEquals(0, set.channelCount())
    }
}
