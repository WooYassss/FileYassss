package com.wooyassss.fileyassss.domain.file.controller

import com.wooyassss.fileyassss.domain.file.dto.reqeust.SaveFileRequest
import com.wooyassss.fileyassss.domain.file.service.FileService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import org.reactivestreams.Publisher
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux


@RestController
class FileController(
    private val fileService: FileService
) {

    @PostMapping("/save")
    suspend fun saveFile(
        @RequestHeader("user-name", required = false) uploader: String,
        @RequestPart("files") files: Flux<FilePart>
    ): Flow<Void> {

        return fileService.saveFile(
            SaveFileRequest(
                files = files.asFlow()
            )
        )
    }
}