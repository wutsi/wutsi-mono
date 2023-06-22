package com.wutsi.blog.app.settings

import com.wutsi.blog.app.model.UploadModel
import com.wutsi.blog.app.service.UploadService
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.multipart.MultipartFile

@Controller()
@RequestMapping("/upload")
class UploadController(private val service: UploadService) {
    @ResponseBody
    @PostMapping
    fun upload(@RequestParam file: MultipartFile): UploadModel = service.upload(file)

    @ResponseBody
    @GetMapping
    fun upload(@RequestParam url: String): UploadModel = service.upload(url)
}
