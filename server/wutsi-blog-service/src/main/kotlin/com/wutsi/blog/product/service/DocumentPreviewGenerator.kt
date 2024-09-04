package com.wutsi.blog.product.service

import java.io.InputStream
import java.io.OutputStream

interface DocumentPreviewGenerator {
    fun generate(`in`: InputStream, out: OutputStream): Boolean
}
