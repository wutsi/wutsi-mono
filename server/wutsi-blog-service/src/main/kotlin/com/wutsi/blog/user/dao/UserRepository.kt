package com.wutsi.blog.user.dao

import com.wutsi.blog.user.domain.UserEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.Date
import java.util.Optional

@Repository
interface UserRepository : CrudRepository<UserEntity, Long> {
    fun findByEmailIgnoreCase(email: String): Optional<UserEntity>
    fun findByNameIgnoreCase(name: String): Optional<UserEntity>

    fun findByAutoFollowByBlogsAndBlog(autoFollowByBlogs: Boolean, blog: Boolean): List<UserEntity>

    fun findByLastPublicationDateTimeGreaterThanAndActiveAndSuspendedAndBlog(
        lastPublicationDateTime: Date,
        active: Boolean,
        suspended: Boolean,
        blog: Boolean,
    ): List<UserEntity>

    fun findByLastPublicationDateTimeLessThanEqualAndActiveAndSuspendedAndBlog(
        lastPublicationDateTime: Date,
        active: Boolean,
        suspended: Boolean,
        blog: Boolean,
    ): List<UserEntity>

    fun findByLastPublicationDateTimeIsNullAndActiveAndSuspendedAndBlog(
        active: Boolean,
        suspended: Boolean,
        blog: Boolean,
    ): List<UserEntity>
}
