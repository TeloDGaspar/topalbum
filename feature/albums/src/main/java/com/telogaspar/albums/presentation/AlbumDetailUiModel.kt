package com.telogaspar.albums.presentation

import com.telogaspar.albums.domain.model.Album
import com.telogaspar.albums.domain.model.AlbumDetails

data class AlbumDetailUiModel(
    val id: String,
    val name: String,
    val artist: String,
    val artworkUrl: String,
    val genre: String,
    val releaseDate: String,
    val price: String,
    val trackCount: Int,
    val rights: String,
    val link: String,
)

internal fun Album.toDetailUiModel(
    details: AlbumDetails,
): AlbumDetailUiModel =
    AlbumDetailUiModel(
        id = id,
        name = name,
        artist = artist,
        artworkUrl = artworkUrl,
        genre = details.genre,
        releaseDate = details.releaseDate,
        price = details.price,
        trackCount = details.trackCount,
        rights = details.rights,
        link = details.link,
    )