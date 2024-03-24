package com.wooyassss.fileyassss.domain.file.service.impl

import com.wooyassss.fileyassss.common.util.IllegalArgsEx
import com.wooyassss.fileyassss.domain.file.domain.FileInfo
import com.wooyassss.fileyassss.domain.file.domain.FileInfoRepository
import com.wooyassss.fileyassss.domain.file.service.FileInfoService
import com.wooyassss.fileyassss.domain.file.service.FileService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@Service
class FileInfoServiceImpl(
    private val repository: FileInfoRepository,
    private val reactiveMongoTemplate: ReactiveMongoTemplate
) : FileInfoService {
    override suspend fun saveFileInfo(fileInfo: FileInfo): FileInfo =
        repository.save(
            fileInfo
        ).awaitSingle()


    override suspend fun findById(id: String): FileInfo =
        repository.findById(id)
            .awaitSingleOrNull()
            ?: IllegalArgsEx("조회된 파일이 없습니다. id = $id")


    override suspend fun findByUploader(uploader: String): Flow<FileInfo> =
        repository.findByUploader(uploader).asFlow()


    override suspend fun findByPath(path: String): FileInfo =
        repository.findByPath(path)
            .awaitSingleOrNull()
            ?: IllegalArgsEx("조회된 파일이 없습니다. path = $path")


    override fun updateUploadedSize(id: String, uploadedSize: Long): FileInfo {
        val query = Query.query(Criteria.where("id").`is`(id))
        val update = Update().set("uploadedSize", uploadedSize).set("updatedAt", LocalDateTime.now())

        return reactiveMongoTemplate
            .findAndModify(query, update, FileInfo::class.java)
            .block()
            ?: IllegalArgsEx("조회된 파일이 없습니다. id = $id")
    }

    override suspend fun updatePath(id: String, path: String): FileInfo {
        val fileInfo = this.findById(id)
        fileInfo.path = path
        return this.saveFileInfo(fileInfo)
    }
}