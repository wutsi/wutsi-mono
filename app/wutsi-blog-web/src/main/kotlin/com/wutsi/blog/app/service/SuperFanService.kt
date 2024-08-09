package com.wutsi.blog.app.service

import com.wutsi.blog.app.backend.SuperFanBackend
import com.wutsi.blog.app.mapper.SuperFanMapper
import com.wutsi.blog.app.model.SuperFanModel
import com.wutsi.blog.app.model.WalletModel
import com.wutsi.blog.transaction.dto.SearchSuperFanRequest
import com.wutsi.blog.user.dto.SearchUserRequest
import org.springframework.stereotype.Component

@Component
class SuperFanService(
    private val backend: SuperFanBackend,
    private val mapper: SuperFanMapper,
    private val userService: UserService,
) {
    fun search(wallet: WalletModel): List<SuperFanModel> {
        val fans = backend.search(
            SearchSuperFanRequest(
                walletId = wallet.id,
                limit = 50
            )
        ).superFans
        val userIds = fans.map { fan -> fan.userId }
        val userMap = if (userIds.isEmpty()) {
            emptyMap()
        } else {
            userService.search(
                SearchUserRequest(
                    userIds = userIds,
                    limit = userIds.size
                )
            ).associateBy { it.id }
        }
        return fans.mapNotNull { fan ->
            fan.userId.let { userMap[fan.userId] }?.let { user ->
                mapper.toSuperFanModel(fan, user, wallet)
            }
        }
    }
}
