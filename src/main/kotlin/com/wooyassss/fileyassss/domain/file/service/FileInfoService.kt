package com.wooyassss.fileyassss.domain.file.service

import com.wooyassss.fileyassss.domain.file.domain.FileInfo
import kotlinx.coroutines.flow.Flow

interface FileInfoService {
    suspend fun saveFileInfo(fileInfo: FileInfo): FileInfo
    suspend fun findById(id: String): FileInfo
    suspend fun findByPath(path: String): FileInfo
    suspend fun findByUploader(uploader: String): Flow<FileInfo>
    suspend fun updatePath(id: String, path: String): FileInfo
    fun updateUploadedSize(id: String, uploadedSize: Long): FileInfo
}