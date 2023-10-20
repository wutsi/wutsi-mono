package com.wutsi.blog.app.service

import com.wutsi.blog.app.backend.ReaderBackend
import com.wutsi.blog.app.model.ReaderModel
import com.wutsi.blog.story.dto.SearchReaderRequest
import com.wutsi.blog.user.dto.SearchUserRequest
import com.wutsi.blog.user.dto.UserSortStrategy
import org.springframework.stereotype.Service

@Service
class ReaderService(
    private val backend: ReaderBackend,
    private val userService: UserService,
) {
    fun search(request: SearchReaderRequest): List<ReaderModel> {
        val readers = backend.search(request).readers
        if (readers.isEmpty()) {
            return emptyList()
        }

        val userMap = userService.search(
            SearchUserRequest(
                userIds = readers.map { it.userId },
                sortBy = UserSortStrategy.NONE,
                limit = readers.size,
            ),
        ).associateBy { it.id }

        return readers.map { reader ->
            userMap[reader.userId]?.let { user ->
                ReaderModel(
                    id = reader.id,
                    subscribed = reader.subscribed,
                    liked = reader.liked,
                    commented = reader.commented,
                    userId = reader.id,
                    user = user,
                    email = reader.email,
                )
            }
        }.filterNotNull()
    }
}
