package com.wooyassss.fileyassss.domain.file.service

import com.wooyassss.fileyassss.common.util.IllegalArgsEx
import com.wooyassss.fileyassss.domain.file.domain.FileInfo
import com.wooyassss.fileyassss.domain.file.domain.FileInfoRepository
import com.wooyassss.fileyassss.domain.file.dto.reqeust.SaveFileRequest
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service
import java.io.File
import java.nio.file.Path
import kotlin.io.path.fileSize

@Service
class FileServiceImpl(
    private val repository: FileInfoRepository,
    @Value("\${base.file.path}") private val basePath: String,
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

    override fun saveFile(req: SaveFileRequest): Flow<FileInfo> {
        return req.files.flatMapMerge {
            saveFilePart(it, req.uploader)
        }
    }

    private suspend fun saveFilePart(filePart: FilePart, uploader: String): Flow<FileInfo> {

        // /$basePath/$uploader
        val saveFilePath = createPath(uploader).resolve(filePart.filename())

        if (saveFilePath.toFile().exists())
            throw FileAlreadyExistsException(saveFilePath.toFile())

        log.info("SaveFile FileName={}", filePart.filename())

        return flow {
            filePart.transferTo(saveFilePath).awaitSingleOrNull()

            emit(
                repository.save(
                    FileInfo(
                        name = filePart.filename(),
                        extension = saveFilePath.toFile().extension,
                        size = saveFilePath.fileSize(),
                        path = saveFilePath.toString(),
                        uploader = uploader,
                    )
                ).awaitSingle()
            )
        }
    }

    private suspend fun createPath(path: String): Path {
        val directory = File("$basePath/$path")

        return if (directory.exists()) {
            directory.toPath()
        } else {
            log.info("Create Path={}", directory.canonicalPath)
            directory.mkdir()
            createPath(path)
        }
    }
}