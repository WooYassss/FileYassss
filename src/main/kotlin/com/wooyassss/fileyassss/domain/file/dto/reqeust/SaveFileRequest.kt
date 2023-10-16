package com.wooyassss.fileyassss.domain.file.dto.reqeust

data class SaveFileRequest(
    val name: String,
    val path: String,
    val signature: String,
    val uploader: String = "Anonymous",
)