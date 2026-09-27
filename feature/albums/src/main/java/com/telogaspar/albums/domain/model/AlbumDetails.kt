package com.telogaspar.albums.domain.model

data class AlbumDetails(
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