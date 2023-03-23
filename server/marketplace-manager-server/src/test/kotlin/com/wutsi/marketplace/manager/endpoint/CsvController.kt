package com.wutsi.marketplace.manager.endpoint

import com.amazonaws.util.IOUtils
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletResponse

@RestController
class CsvController {
    @GetMapping("/{name}.csv", produces = ["application/csv"])
    fun products(@PathVariable name: String, response: HttpServletResponse) {
        val input = CsvController::class.java.getResourceAsStream("/$name.csv")
        IOUtils.copy(input, response.outputStream)
    }
}
