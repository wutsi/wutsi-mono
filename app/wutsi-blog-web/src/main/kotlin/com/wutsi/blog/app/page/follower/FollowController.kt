package com.wutsi.blog.app.page.follower

import com.wutsi.blog.app.backend.FollowerBackend
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.client.follower.CreateFollowerRequest
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping
class FollowController(private val api: FollowerBackend, private val requestContext: RequestContext) {
    @GetMapping("/follow")
    fun follow(
        @RequestParam userId: Long,
        @RequestParam(name = "return") returnUrl: String,
    ): String {
        api.create(
            CreateFollowerRequest(
                userId = userId,
                followerUserId = requestContext.currentUser()!!.id,
            ),
        )
        return "redirect:$returnUrl"
    }
}
