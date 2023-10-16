package com.wooyassss.fileyassss.domain.file.domain

import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface FileRepository : ReactiveCrudRepository<File, String> {
    fun findByUploader(uploader: String): Flux<File>
    fun findByPath(path: String): Mono<File>
}