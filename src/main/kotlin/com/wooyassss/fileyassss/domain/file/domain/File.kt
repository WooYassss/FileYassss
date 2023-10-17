package com.wooyassss.fileyassss.domain.file.domain

import org.springframework.beans.factory.annotation.Value
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate

@Document("Files")
data class File(
    @Id @Value("file_id")
    val id: String? = null,
    var name: String,
    var extension: String,
    var size: Long,
    var path: String,
    val uploader: String,
    // 시간 저장
    val createdAt: LocalDate,
    var updatedAt: LocalDate
)