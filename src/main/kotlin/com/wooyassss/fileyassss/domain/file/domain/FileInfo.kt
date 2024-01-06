package com.wooyassss.fileyassss.domain.file.domain

import org.springframework.beans.factory.annotation.Value
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document("FileInfos")
data class FileInfo(
    @Id @Value("id")
    val id: String? = null,
    var name: String,
    var extension: String,
    var path: String? = null,
    var size: Long,
    var uploadedSize: Long = 0,
    val uploader: String,
    @CreatedDate
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @LastModifiedDate
    var updatedAt: LocalDateTime = LocalDateTime.now()
)