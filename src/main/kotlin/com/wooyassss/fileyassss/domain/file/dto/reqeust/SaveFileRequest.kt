package com.wooyassss.fileyassss.domain.file.dto.reqeust

import org.springframework.http.codec.multipart.FilePart
import reactor.core.publisher.Flux

data class SaveFileRequest(
    val uploader: String? = "Anonymous",
    val files: Flux<FilePart>
)