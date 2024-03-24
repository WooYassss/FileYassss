package com.wooyassss.fileyassss.domain.file.service.impl

import com.wooyassss.fileyassss.domain.file.domain.FileInfo
import com.wooyassss.fileyassss.domain.file.service.FileInfoService
import com.wooyassss.fileyassss.domain.file.service.FileService
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactive.collect
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import kotlin.io.path.pathString

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
        log.info("File upload started for ID: ${fileInfo.id}, Uploader: $uploader")

        var totalBytesWritten = 0L
        val chunkSize = 8 * 1024 * 1024 // 8MB

        file.content()
            .publishOn(Schedulers.boundedElastic())
            .map { dataBuffer ->
                val byteArray = ByteArray(dataBuffer.readableByteCount())
                dataBuffer.read(byteArray)
                DataBufferUtils.release(dataBuffer)
                byteArray
            }
            .bufferUntil({ it.size >= chunkSize }, true)
            .flatMap { buffers ->
                val chunkBytes = buffers.flatMap { it.toList() }.toByteArray()
                Files.write(tempFile, chunkBytes, StandardOpenOption.APPEND)
                totalBytesWritten += chunkBytes.size

                fileInfo = fileInfoService.updateUploadedSize(fileInfo.id!!, totalBytesWritten)
                val uploadProgress = (totalBytesWritten.toDouble() / fileInfo.size) * 100
                log.info("File chunk saved: $tempFile, progress: $uploadProgress%")

                Mono.just(uploadProgress)
            }.collectList() // 모든 진행률을 수집 (필요한 경우)
            .awaitSingle()

        // 파일 업로드가 완료된 후의 최종 FileInfo 업데이트
        fileInfo = fileInfoService.updateUploadedSize(fileInfo.id!!, totalBytesWritten)

        val finalUploadProgress = (totalBytesWritten.toDouble() / fileInfo.size) * 100
        log.info("File upload completed: $tempFile.pathString, final progress: $finalUploadProgress%")

        return fileInfo
    }


    private suspend fun createTempFile(uploader: String, fileId: String): Path {
        val userPath = createPath(uploader)
        return Files.createTempFile(userPath, "fileYass-", ".$fileId")
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

