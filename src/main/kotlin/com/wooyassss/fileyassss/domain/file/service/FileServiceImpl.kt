package com.wooyassss.fileyassss.domain.file.service

import com.wooyassss.fileyassss.common.util.IllegalArgsEx
import com.wooyassss.fileyassss.domain.file.domain.FileInfo
import com.wooyassss.fileyassss.domain.file.domain.FileInfoRepository
import com.wooyassss.fileyassss.domain.file.dto.reqeust.SaveFileRequest
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service
import java.io.File
import java.nio.file.Path
import java.sql.Timestamp
import java.time.LocalDateTime
import kotlin.io.path.fileSize
import kotlin.random.Random
import kotlin.random.nextInt

@Service
class FileServiceImpl(
    @Value("\${base.file.path}")
    private val basePath: String,
    private val repository: FileInfoRepository,
) : FileService {

    private val log = LoggerFactory.getLogger(javaClass)

    override suspend fun findById(id: String): FileInfo =
        repository.findById(id)
            .awaitSingleOrNull()
            ?: IllegalArgsEx("조회된 파일이 없습니다. id = $id")


    override fun findByUploader(uploader: String): Flow<FileInfo> =
        repository.findByUploader(uploader).asFlow()

    override suspend fun findByPath(path: String): FileInfo =
        repository.findByPath(path)
            .awaitSingleOrNull()
            ?: IllegalArgsEx("조회된 파일이 없습니다. path = $path")

    override fun saveFile(req: SaveFileRequest): Flow<FileInfo> =
        req.files.flatMapConcat { file ->
            // /$basePath/$uploader
            val fileId = createFileId()
            val filePath = createPath(req.uploader).resolve(fileId)

            saveFilePart(file, filePath)
            saveFileInfo(file, filePath, req.uploader)
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

    private suspend fun createFileId(): String {
        val timestamp = Timestamp.valueOf(LocalDateTime.now())
        val random = Random(timestamp.time).nextInt(10..40)

        val fileId = timestamp.time.toString() + random.toString()
        log.info("createFileId ={}", fileId)
        return fileId
    }

    private suspend fun saveFilePart(
        file: FilePart,
        filePath: Path
    ) {
        log.info("SaveFile FileName={}", file.filename())
        file.transferTo(filePath).awaitSingleOrNull()
    }

    private suspend fun saveFileInfo(
        file: FilePart,
        filePath: Path,
        uploader: String
    ): Flow<FileInfo> =
        repository.save(
            FileInfo(
                name = file.filename(),
                extension = filePath.toFile().extension,
                size = filePath.fileSize(),
                path = filePath.toString(),
                uploader = uploader,
            )
        ).asFlow()
}

