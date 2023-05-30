package com.wutsi.blog.app.security.model

enum class Permission {
    editor, // Can edit the story
    reader, // Can read the story
    previewer, // Can preview the story
    owner, // Is the owner of the story
}
