package com.telogaspar.albums.presentation

import kotlinx.serialization.Serializable

@Serializable
data object AlbumListRoute

@Serializable
data class AlbumDetailRoute(
    val albumId: String,
) {
    companion object {
        const val ALBUM_ID_KEY = "albumId"
    }
}