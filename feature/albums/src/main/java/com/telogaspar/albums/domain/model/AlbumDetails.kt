package com.telogaspar.albums.domain.model

data class AlbumDetails(
    val genre: String,
    val releaseDate: String,
    val price: String,
    val trackCount: Int,
    val rights: String,
    val link: String,
)