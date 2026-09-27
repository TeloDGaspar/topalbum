package com.telogaspar.albums.domain.repository

import com.telogaspar.albums.domain.model.Album
import com.telogaspar.albums.domain.model.AlbumDetails
import kotlinx.coroutines.flow.Flow

interface AlbumListRepository {
    suspend fun getTopAlbums(): List<Album>

    suspend fun getDetailAlbum(albumId: String): AlbumDetails
}