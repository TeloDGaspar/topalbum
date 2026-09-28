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
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

internal class AlbumListRepositoryImpl @Inject constructor(
    private val remoteDataSource: TopAlbumsListRemoteDataSource,
    private val albumMapper: AlbumMapper,
    private val albumDetailsMapper: AlbumDetailsMapper
    ) : AlbumListRepository {

    private var cachedEntries: List<AlbumDto>? = null

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    override suspend fun getTopAlbums(): List<Album> =
        mapErrors {
            albumMapper.map(getEntries())
        }

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    override suspend fun getDetailAlbum(albumId: String): AlbumDetails =
        mapErrors {
            val dto = getEntries()
                .firstOrNull { it.id.attributes.id == albumId }
                ?: throw AlbumException.NotFoundException(albumId)

            albumDetailsMapper.map(dto)
        }

    private suspend fun getEntries(): List<AlbumDto> {
        cachedEntries?.let { return it }

        return remoteDataSource.fetchAlbumList()
            .feed
            .entry
            .also { cachedEntries = it }
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

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    private suspend fun <T> mapErrors(
        block: suspend () -> T,
    ): T =
        try {
            block()
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            throw exception.toAlbumException()
        }
}
