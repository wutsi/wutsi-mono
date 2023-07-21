package com.wutsi.blog.story.tools

import com.wutsi.blog.story.dao.StoryRepository
import com.wutsi.blog.story.dto.StoryStatus
import com.wutsi.blog.story.service.StoryNLPService
import org.springframework.scheduling.annotation.Async
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/stories/commands/store-bag-of-words")
class StoreStoryBagOfWordsCommandExecutor(
    private val dao: StoryRepository,
    private val service: StoryNLPService,
) {
    @Async
    @GetMapping
    fun execute() {
        dao.findByStatus(StoryStatus.PUBLISHED).forEach {
            service.storeBagOfWords(it)
        }
    }
}
