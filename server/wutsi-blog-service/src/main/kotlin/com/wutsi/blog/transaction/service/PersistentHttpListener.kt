package com.wutsi.blog.transaction.service

import com.wutsi.blog.transaction.dao.TransactionEventRepository
import com.wutsi.blog.transaction.domain.TransactionEventEntity
import com.wutsi.platform.payment.core.HttpListener
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PersistentHttpListener(private val dao: TransactionEventRepository) : HttpListener {
    @Transactional
    override fun notify(
        transactionId: String,
        method: String,
        uri: String,
        statusCode: Int,
        request: String?,
        response: String?,
    ) {
        dao.save(
            TransactionEventEntity(
                transactionId = transactionId,
                method = method,
                uri = uri,
                statusCode = statusCode,
                request = request,
                response = response
            )
        )
    }
}
