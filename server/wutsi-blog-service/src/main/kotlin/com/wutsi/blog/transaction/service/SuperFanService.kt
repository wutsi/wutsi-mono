package com.wutsi.blog.transaction.service

import com.wutsi.blog.transaction.dao.SearchSuperFanQueryBuilder
import com.wutsi.blog.transaction.domain.SuperFanEntity
import com.wutsi.blog.transaction.dto.SearchSuperFanRequest
import com.wutsi.blog.util.Predicates
import com.wutsi.platform.core.logging.KVLogger
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Service

@Service
class SuperFanService(
    private val logger: KVLogger,
    private val em: EntityManager,
) {
    fun search(request: SearchSuperFanRequest): List<SuperFanEntity> {
        logger.add("request_wallet_id", request.walletId)
        logger.add("request_user_id", request.userId)
        logger.add("request_limit", request.limit)
        logger.add("request_offset", request.offset)

        val builder = SearchSuperFanQueryBuilder()
        val sql = builder.query(request)
        val params = builder.parameters(request)
        val query = em.createNativeQuery(sql, SuperFanEntity::class.java)
        Predicates.setParameters(query, params)
        return query.resultList as List<SuperFanEntity>
    }
}
