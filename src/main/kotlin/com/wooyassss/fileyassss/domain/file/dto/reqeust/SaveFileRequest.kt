package com.wooyassss.fileyassss.domain.file.dto.reqeust

import kotlinx.coroutines.flow.Flow
import org.springframework.http.codec.multipart.FilePart

data class SaveFileRequest(
    val uploader: String,
    val files: Flow<FilePart>
)