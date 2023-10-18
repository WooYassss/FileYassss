package com.wooyassss.fileyassss.domain.file.service

import com.wooyassss.fileyassss.common.util.IllegalArgsEx
import com.wooyassss.fileyassss.domain.file.domain.FileInfo
import com.wooyassss.fileyassss.domain.file.domain.FileInfoRepository
import com.wooyassss.fileyassss.domain.file.dto.reqeust.SaveFileRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.stereotype.Service
import kotlin.math.log

@Service
class FileServiceImpl (
    private val repository: FileInfoRepository
) : FileService {

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

    override suspend fun saveFile(req: SaveFileRequest): Flow<FileInfo> {
//        req.files.doOnNext { println(it.filename()) }.flatMap {  }

    }
}