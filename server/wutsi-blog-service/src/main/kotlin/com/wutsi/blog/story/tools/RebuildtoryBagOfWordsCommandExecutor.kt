package com.wutsi.blog.story.tools

import com.wutsi.blog.story.service.StoryService
import org.springframework.scheduling.annotation.Async
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/stories/commands/rebuild-bag-of-words")
class RebuildtoryBagOfWordsCommandExecutor(
    private val service: StoryService,
) {
    @Async
    @GetMapping
    fun execute() {
        service.generateCorpusBagOfWords(true)
    }
}
