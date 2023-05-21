package com.wutsi.blog.like.dao

import com.wutsi.blog.like.domain.LikeV0
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Deprecated("")
@Repository
interface LikeV0Repository : CrudRepository<LikeV0, Long>
