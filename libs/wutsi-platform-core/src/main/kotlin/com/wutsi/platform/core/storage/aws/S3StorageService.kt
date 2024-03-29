package com.wutsi.platform.core.storage.aws

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.GetObjectRequest
import com.amazonaws.services.s3.model.ListObjectsRequest
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.PutObjectRequest
import com.wutsi.platform.core.storage.StorageService
import com.wutsi.platform.core.storage.StorageVisitor
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.URL

open class S3StorageService(
    private val s3: AmazonS3,
    private val bucket: String,
) : StorageService {

    override fun contains(url: URL) = url.toString().startsWith(urlPrefix())

    @Throws(IOException::class)
    override fun store(
        path: String,
        content: InputStream,
        contentType: String?,
        ttlSeconds: Int?,
        contentEncoding: String?,
        contentLength: Long?,
    ): URL {
        val meta = ObjectMetadata()
        contentType?.let { meta.contentType = it }
        ttlSeconds?.let { meta.cacheControl = "max-age=$it, must-revalidate" }
        contentEncoding?.let { meta.contentEncoding = it }
        contentLength?.let { meta.contentLength = it }

        val request = PutObjectRequest(bucket, path, content, meta)
        try {
            s3.putObject(request)
            return toURL(path)
        } catch (e: Exception) {
            throw IOException(String.format("Unable to store to s3://%s/%s", bucket, path), e)
        }
    }

    override fun get(url: URL, os: OutputStream) {
        val path = url.path.substring(bucket.length + 2)
        val request = GetObjectRequest(bucket, path)
        try {
            val obj = s3.getObject(request)
            obj.use {
                obj.objectContent.copyTo(os)
            }
        } catch (e: Exception) {
            throw IOException(String.format("Unable to get s3://%s/%s", bucket, path), e)
        }
    }

    override fun exists(url: URL): Boolean {
        val path = url.path.substring(bucket.length + 2)
        val request = GetObjectRequest(bucket, path)
        try {
            s3.getObject(request)
            return true
        } catch (e: Exception) {
            return false
        }
    }

    override fun visit(path: String, visitor: StorageVisitor) {
        val request = ListObjectsRequest()
        request.bucketName = bucket
        request.prefix = path
        val listings = s3.listObjects(request)
        listings.objectSummaries.forEach { visitor.visit(toURL(it.key)) }
    }

    override fun toURL(path: String) = URL(urlPrefix() + "/$path")

    private fun urlPrefix() = "https://s3.amazonaws.com/$bucket"
}
