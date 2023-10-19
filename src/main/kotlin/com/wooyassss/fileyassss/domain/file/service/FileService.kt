package com.wooyassss.fileyassss.domain.file.service

import com.wooyassss.fileyassss.domain.file.domain.FileInfo
import com.wooyassss.fileyassss.domain.file.dto.reqeust.SaveFileRequest
import kotlinx.coroutines.flow.Flow

interface FileService {
    suspend fun findById(id: String): FileInfo
    suspend fun findByPath(path: String): FileInfo
    fun findByUploader(uploader: String): Flow<FileInfo>
    fun saveFile(req: SaveFileRequest): Flow<Void>
}