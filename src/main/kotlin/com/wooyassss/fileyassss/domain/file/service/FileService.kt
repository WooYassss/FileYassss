package com.wooyassss.fileyassss.domain.file.service

import com.wooyassss.fileyassss.domain.file.domain.FileInfo
import org.springframework.http.codec.multipart.FilePart

interface FileService {
    suspend fun saveFile(uploader: String, fileId: String, file: FilePart): FileInfo
}