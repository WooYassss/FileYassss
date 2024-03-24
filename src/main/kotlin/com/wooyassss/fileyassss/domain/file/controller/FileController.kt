package com.wooyassss.fileyassss.domain.file.controller

import com.wooyassss.fileyassss.domain.file.domain.FileInfo
import com.wooyassss.fileyassss.domain.file.dto.reqeust.UploadFileInfoRequest
import com.wooyassss.fileyassss.domain.file.dto.response.UploadFileResponse
import com.wooyassss.fileyassss.domain.file.service.FileInfoService
import com.wooyassss.fileyassss.domain.file.service.FileService
import org.slf4j.LoggerFactory
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController


@RestController

class FileController(
    private val fileService: FileService,
    private val fileInfoService: FileInfoService,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @PostMapping("/upload-file-info")
    suspend fun uploadFileInfo(
        @RequestBody request: UploadFileInfoRequest
    ): String {
        val fileInfo = FileInfo(
            name = request.name,
            extension = request.extension,
            size = request.size,
            uploader = request.uploader
        )
        val savedFileInfo = fileInfoService.saveFileInfo(fileInfo)

        log.info("FileInfo Save Successfully: $savedFileInfo.id")

        return savedFileInfo.id!!
    }

    @PostMapping("/upload")
    suspend fun uploadFile(
        @RequestHeader fileId: String,
        @RequestPart("file") file: FilePart
    ): UploadFileResponse {
        val savedFile = fileService.saveFile("GGOS3", fileId, file)

        val uploadProgress = if (savedFile.uploadedSize > 0 && savedFile.size > 0) {
            (savedFile.uploadedSize.toDouble() / savedFile.size) * 100
        } else {
            0.0 // 업로드된 크기나 파일 크기가 0일 경우, 진행률도 0으로 설정
        }

        log.info("FileUpload Success :${savedFile.id!!}, Progress: $uploadProgress%")
        return UploadFileResponse(savedFile.id, uploadProgress)
    }

}