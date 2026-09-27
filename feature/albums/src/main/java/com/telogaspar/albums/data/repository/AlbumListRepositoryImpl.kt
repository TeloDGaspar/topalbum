package com.telogaspar.albums.data.repository

import android.os.Build
import androidx.annotation.RequiresExtension
import com.telogaspar.albums.data.mapper.AlbumDetailsMapper
import com.telogaspar.albums.data.mapper.AlbumMapper
import com.telogaspar.albums.data.remote.AlbumDto
import com.telogaspar.albums.data.remote.TopAlbumsListRemoteDataSource
import com.telogaspar.albums.domain.exception.AlbumException
import com.telogaspar.albums.domain.repository.AlbumListRepository
import com.telogaspar.albums.domain.model.Album
import com.telogaspar.albums.domain.model.AlbumDetails
import java.io.IOException
import retrofit2.HttpException

internal class AlbumListRepositoryImpl(
    private val remoteDataSource: TopAlbumsListRemoteDataSource,
    private val albumMapper: AlbumMapper,
    private val albumDetailsMapper: AlbumDetailsMapper
    ) : AlbumListRepository {

    private var cachedEntries: List<AlbumDto>? = null

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    override suspend fun getTopAlbums(page: Int, limit: Int): List<Album> {
        return runCatching {
            val entries = getEntries()

            if (entries.isEmpty()) {
                throw AlbumException.EmptyResultException()
            }

            albumMapper.map(entries)
        }.getOrElse { throwable ->
            throw throwable.toAlbumException()
        }

    }

    override suspend fun getDetailAlbum(albumId: String): AlbumDetails {
        val albumDto = getEntries()
            .firstOrNull { it.id.attributes.id == albumId }
            ?: throw AlbumException.NotFoundException(albumId)

        return albumDetailsMapper.map(albumDto)
    }

    private suspend fun getEntries(): List<AlbumDto> {
        cachedEntries?.let { return it }

        val response = remoteDataSource.fetchAlbumList()

        return response.feed.entry.also {
            cachedEntries = it
        }
    }

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    private fun Throwable.toAlbumException(): AlbumException =
        when (this) {
            is AlbumException -> this
            is HttpException -> AlbumException.ApiException(
                code = code(),
                cause = this,
            )
            is IOException -> AlbumException.NetworkException(this)
            else -> AlbumException.UnknownException(this)
        }
}
