package com.wutsi.blog.story.endpoint

import com.wutsi.blog.story.service.ReaderService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@RequestMapping("/v1/readers/commands/replay")
class ReplayReaderCommandExecutor(
    private val service: ReaderService,
) {
    @GetMapping
    fun replay(@RequestParam year: Int) {
        var date = LocalDate.of(year, 1, 1)
        while (date.year == year) {
            service.importMonthlyReaders(date)
            date = date.plusMonths(1)
        }
    }
}
