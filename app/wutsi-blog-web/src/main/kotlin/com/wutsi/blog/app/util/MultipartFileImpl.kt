package com.wutsi.blog.app.util

import org.apache.commons.io.FilenameUtils
import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayInputStream
import java.io.File
import java.net.HttpURLConnection

class MultipartFileImpl(
    private val bytes: ByteArray,
    private val cnn: HttpURLConnection,
) : MultipartFile {
    override fun getName() = ""

    override fun isEmpty() = false

    override fun getSize() = cnn.contentLengthLong

    override fun getBytes() = bytes

    override fun getOriginalFilename() = FilenameUtils.getName(cnn.url.file)

    override fun getInputStream() = ByteArrayInputStream(bytes)

    override fun getContentType() = cnn.contentType

    override fun transferTo(file: File) {
        TODO("not implemented")
    }
}
