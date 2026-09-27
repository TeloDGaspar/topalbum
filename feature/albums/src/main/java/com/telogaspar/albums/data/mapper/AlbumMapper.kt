package com.telogaspar.albums.data.mapper

import com.telogaspar.albums.data.remote.AlbumDto
import com.telogaspar.albums.domain.model.Album
import com.telogaspar.albums.domain.model.AlbumDetails
import javax.inject.Inject
import kotlin.collections.map
import com.telogaspar.core.mapper.Mapper

internal class AlbumMapper @Inject constructor() :
    Mapper<List<AlbumDto>, List<Album>> {

    override fun map(input: List<AlbumDto>): List<Album> =
        input.map { dto ->
            Album(
                id = dto.id.attributes.id,
                name = dto.name.label,
                artist = dto.artist.label,
                artworkUrl = dto.images.lastOrNull()?.label.orEmpty(),
            )
        }
}


internal class AlbumDetailsMapper @Inject constructor() :
    Mapper<AlbumDto, AlbumDetails> {

    override fun map(input: AlbumDto): AlbumDetails =
        AlbumDetails(
            genre = input.category.attributes.label,
            releaseDate = input.releaseDate.attributes.label,
            price = input.price.label,
            trackCount = input.itemCount.label.toIntOrNull() ?: 0,
            rights = input.rights.label,
            link = input.link.attributes.href,
        )
}