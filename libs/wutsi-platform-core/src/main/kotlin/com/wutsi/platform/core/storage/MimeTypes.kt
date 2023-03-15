package com.wutsi.platform.core.storage

import java.nio.file.Files
import java.nio.file.Path

class MimeTypes {
    fun detect(path: String): String {
        val i = path.lastIndexOf('.')
        val extension = if (i > 0) path.substring(i + 1) else ""
        if (extension == "jpg" || extension == "jpeg") {
            return "image/jpeg"
        } else if (extension == "png") {
            return "image/png"
        } else if (extension == "gif") {
            return "image/gif"
        } else if (extension == "webp") {
            return "image/webp"
        } else {
            return Files.probeContentType(Path.of(path)) ?: "application/octet-stream"
        }
    }
}
