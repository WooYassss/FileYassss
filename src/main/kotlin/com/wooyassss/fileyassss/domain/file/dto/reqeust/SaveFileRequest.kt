package com.wooyassss.fileyassss.domain.file.dto.reqeust

data class SaveFileRequest(
    val fileName: String,
    val uploader: String = "Anonymous",
)