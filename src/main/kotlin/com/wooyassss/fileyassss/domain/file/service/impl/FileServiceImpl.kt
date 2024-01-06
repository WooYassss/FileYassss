package com.wooyassss.fileyassss.domain.file.service.impl

import com.wooyassss.fileyassss.domain.file.domain.FileInfo
import com.wooyassss.fileyassss.domain.file.service.FileInfoService
import com.wooyassss.fileyassss.domain.file.service.FileService
import kotlinx.coroutines.reactive.awaitSingle
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service
import reactor.core.scheduler.Schedulers
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption

@Service
class FileServiceImpl(
    @Value("\${base.file.path}")
    private val basePath: String,
    private val fileInfoService: FileInfoService,
) : FileService {

    private val log = LoggerFactory.getLogger(javaClass)

    override suspend fun saveFile(
        uploader: String,
        fileId: String,
        file: FilePart
    ): FileInfo {
        var fileInfo = fileInfoService.findById(fileId)
        val tempFile = createTempFile(uploader, fileId)

        file.content()
            .publishOn(Schedulers.boundedElastic())
            .doOnNext { dataBuffer ->
                val byteArray = ByteArray(dataBuffer.readableByteCount())
                Files.write(tempFile, byteArray, StandardOpenOption.APPEND)
                DataBufferUtils.release(dataBuffer)

                log.info("byteArraySize :${byteArray.size}")
                log.info("fileInfo.uploadedSize :${fileInfo.uploadedSize}")
                val uploadedSize = fileInfo.uploadedSize + byteArray.size
                fileInfo = fileInfoService.updateUploadedSize(fileInfo.id!!, uploadedSize)

                val uploadProgress = (uploadedSize / fileInfo.size) * 100
                log.info("FileSave Successfully: $tempFile.pathString, progress: $uploadProgress%")
            }.awaitSingle()

        return fileInfo
    }

    private suspend fun createTempFile(uploader: String, fileId: String): Path {
        val userPath = createPath(uploader)
        return Files.createTempFile(userPath, "fileYass-", fileId)
    }

    private suspend fun createPath(path: String): Path {
        val directory = File("$basePath/$path")

        return if (directory.exists())
            directory.toPath()
        else {
            log.info("Create Path={}", directory.canonicalPath)
            directory.mkdir()
            createPath(path)
        }
    }
}

