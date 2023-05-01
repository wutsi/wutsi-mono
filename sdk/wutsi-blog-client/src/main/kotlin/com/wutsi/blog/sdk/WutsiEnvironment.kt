package com.wutsi.blog.sdk

enum class WutsiEnvironment(
    val apiUrl: String,
    val trackUrl: String,
) {
    local("http://localhost:8080", "http://localhost:8082"),
    prod("https://com-wutsi-blog.herokuapp.com", "https://com-wutsi-track.herokuapp.com"),
    int("https://int-com-wutsi-blog.herokuapp.com", "https://int-com-wutsi-track.herokuapp.com"),
}
