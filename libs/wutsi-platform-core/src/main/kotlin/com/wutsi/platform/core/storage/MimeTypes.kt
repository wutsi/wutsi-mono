package com.wutsi.platform.core.storage

import java.nio.file.Files
import java.nio.file.Path

class MimeTypes {
    companion object {
        const val JPEG = "image/jpeg"
        const val GIF = "image/gif"
        const val PNG = "image/png"
        const val WEBP = "image/webp"

        const val DOC = "application/msword"
        const val DOCX = "application/vnd.openxmlformat"
        const val XLS = "application/vnd.ms-excel"
        const val PPT = "application/vnd.ms-powerpoint"
        const val PDF = "application/pdf"
        const val CBZ = "application/x-cdisplay"
        const val EPUB = "application/epub+zip"
        const val JSON = "application/json"
        const val ZIP = "application/x-zip"
        const val OCTET_STREAM = "application/octet-stream"
        const val TXT = "text/plain"
        const val HTML = "text/html"
        const val XML = "text/xml"
    }

    fun extension(path: String): String {
        val i = path.lastIndexOf('.')
        return if (i > 0) path.substring(i + 1).lowercase() else ""
    }

    fun detect(path: String): String {
        val ext = extension(path)
        return when (ext) {
            // Images
            "jpg", "jpeg" -> JPEG
            "png" -> PNG
            "gif" -> GIF
            "webp" -> WEBP

            // Application
            "doc" -> DOC
            "docx" -> DOCX
            "cbz" -> CBZ
            "epub" -> EPUB
            "json" -> JSON
            "pdf" -> PDF
            "ppt", "pptx" -> PPT
            "xls", "xlsx" -> XLS
            "zip" -> ZIP

            // Text
            "txt" -> TXT
            "htm", "html" -> HTML
            "xml" -> XML

            else -> Files.probeContentType(Path.of(path)) ?: OCTET_STREAM
        }
    }

    fun isImage(path: String): Boolean =
        detect(path).startsWith("image/")
}
