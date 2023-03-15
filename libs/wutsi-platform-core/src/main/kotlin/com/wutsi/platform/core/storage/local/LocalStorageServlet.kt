package com.wutsi.platform.core.storage.local

import com.wutsi.platform.core.storage.MimeTypes
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import javax.servlet.ServletException
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class LocalStorageServlet(
    private val directory: String,
) : HttpServlet() {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(LocalStorageServlet::class.java)
    }

    private val mimes: MimeTypes = MimeTypes()

    @Throws(ServletException::class, IOException::class)
    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        val file = File("$directory${req.pathInfo}")
        try {
            output(file, resp)
        } catch (e: FileNotFoundException) {
            resp.sendError(404)
            LOGGER.error("File not found: $file", e)
        } catch (e: Exception) {
            resp.sendError(500)
            LOGGER.error("Unexpected error while processing file $file", e)
        }
    }

    private fun output(file: File, resp: HttpServletResponse) {
        resp.contentType = probeContentType(file)

        FileInputStream(file).use { `in` ->
            `in`.copyTo(resp.outputStream)
        }
    }

    private fun probeContentType(file: File): String {
        return mimes.detect(file.absolutePath)
    }
}
