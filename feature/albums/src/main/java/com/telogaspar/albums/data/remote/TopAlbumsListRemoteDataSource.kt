package com.telogaspar.albums.data.remote

import com.telogaspar.albums.data.api.TopAlbumsApi
import javax.inject.Inject

interface TopAlbumsListRemoteDataSource {
    suspend fun fetchAlbumList(): TopAlbumsResponseDto
}

internal class TopAlbumsListRemoteDataSourceImpl @Inject constructor(
    private val topAlbumsApi: TopAlbumsApi
) : TopAlbumsListRemoteDataSource{

    override suspend fun fetchAlbumList(
    ): TopAlbumsResponseDto {
        return topAlbumsApi.getTopAlbums()
    }
}