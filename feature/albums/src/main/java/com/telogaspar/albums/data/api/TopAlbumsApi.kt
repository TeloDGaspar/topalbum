package com.telogaspar.albums.data.api

import com.telogaspar.albums.data.remote.TopAlbumsResponseDto
import retrofit2.http.GET

internal interface TopAlbumsApi {
    @GET("us/rss/topalbums/limit=100/json")
    suspend fun getTopAlbums(): TopAlbumsResponseDto
}