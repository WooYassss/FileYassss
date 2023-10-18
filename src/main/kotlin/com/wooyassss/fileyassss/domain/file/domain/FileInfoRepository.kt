package com.wooyassss.fileyassss.domain.file.domain

import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface FileInfoRepository : ReactiveCrudRepository<FileInfo, String> {
    fun findByUploader(uploader: String): Flux<FileInfo>
    fun findByPath(path: String): Mono<FileInfo>
}