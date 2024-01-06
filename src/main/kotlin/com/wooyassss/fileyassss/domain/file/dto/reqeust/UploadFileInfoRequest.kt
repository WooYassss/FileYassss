package com.wooyassss.fileyassss.domain.file.dto.reqeust

data class UploadFileInfoRequest(
    var name: String,
    var extension: String,
    var size: Long,
    val uploader: String,
)