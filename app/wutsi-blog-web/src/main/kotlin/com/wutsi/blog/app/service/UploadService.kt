package com.wutsi.blog.app.service

import com.wutsi.blog.app.model.UploadModel
import com.wutsi.blog.app.util.MultipartFileImpl
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.exception.BadRequestException
import com.wutsi.platform.core.error.exception.NotFoundException
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.storage.StorageService
import org.apache.commons.io.FilenameUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.time.Clock
import java.util.Date
import java.util.TimeZone
import java.util.UUID
import javax.imageio.ImageIO

@Service
class UploadService(
    private val clock: Clock,
    private val logger: KVLogger,
    private val storage: StorageService,
) {
    companion object {
        private const val USER_AGENT =
            "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2"
        private const val TTL_1_YEAR = 31536000
        private val LOGGER = LoggerFactory.getLogger(UploadService::class.java)
    }

    fun upload(file: MultipartFile): UploadModel {
        val path = key(file)
        val url = storage.store(path, file.inputStream, file.contentType, TTL_1_YEAR)

        var width = -1
        var height = -1
        if (file.contentType?.startsWith("image/") == true) {
            try {
                val img: BufferedImage = ImageIO.read(file.inputStream)
                width = img.width
                height = img.height
            } catch (ex: Exception) {
                LOGGER.warn("Unable to load image ${file.originalFilename}", ex)
            }
        }

        logger.add("Url", url)
        logger.add("FileName", file.originalFilename)
        logger.add("ImageWidth", width)
        logger.add("ImageHeight", height)
        return UploadModel(
            url = url.toString(),
            width = width,
            height = height,
        )
    }

    fun upload(url: String) = upload(toMultipartFile(url))

    private fun toMultipartFile(url: String): MultipartFile {
        val cnn = URL(url).openConnection() as HttpURLConnection
        try {
            cnn.setRequestProperty("User-Agent", USER_AGENT)
            return toMultipartFile(cnn)
        } finally {
            cnn.disconnect()
        }
    }

    private fun toMultipartFile(cnn: HttpURLConnection): MultipartFile {
        val code = cnn.responseCode
        if (code == HttpURLConnection.HTTP_OK) {
            val input = cnn.inputStream
            input.use {
                val output = ByteArrayOutputStream()
                cnn.inputStream.copyTo(output)

                return MultipartFileImpl(output.toByteArray(), cnn)
            }
        } else {
            throw handleResponseCode(code, cnn.url)
        }
    }

    private fun handleResponseCode(code: Int, url: URL): Exception {
        if (code == HttpURLConnection.HTTP_NOT_FOUND) {
            return NotFoundException(Error("url_not_found"))
        } else if (code == HttpURLConnection.HTTP_BAD_REQUEST) {
            return BadRequestException(Error("bad_request"))
        } else {
            return IOException("url=$url - status=$code")
        }
    }

    private fun key(file: MultipartFile): String {
        val fmt = SimpleDateFormat("yyyy/MM/dd/HH")
        fmt.timeZone = TimeZone.getTimeZone("UTC")

        val prefix = fmt.format(Date(clock.millis()))
        var extension = FilenameUtils.getExtension(file.originalFilename)
        if (extension.isNotEmpty()) {
            extension = ".$extension"
        }
        return "upload/$prefix/${UUID.randomUUID()}$extension"
    }
}
