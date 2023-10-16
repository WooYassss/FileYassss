package com.wooyassss.fileyassss.domain.file.service

import com.wooyassss.fileyassss.domain.file.domain.File
import com.wooyassss.fileyassss.domain.file.dto.reqeust.SaveFileRequest
import org.springframework.stereotype.Service

@Service
interface FileService {
    suspend fun saveFile(req: SaveFileRequest): File
    suspend fun findById(id: String): File
    suspend fun findByUploader(uploader: String): List<File>
    suspend fun findByPath(path: String): File
}