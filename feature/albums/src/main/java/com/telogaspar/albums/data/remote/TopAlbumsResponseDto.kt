package com.telogaspar.albums.data.remote

import com.google.gson.annotations.SerializedName

data class TopAlbumsResponseDto(
    val feed: FeedDto
)

data class FeedDto(
    val entry: List<AlbumDto>
)

data class AlbumDto(
    @SerializedName("im:name")
    val name: LabelDto,

    @SerializedName("im:image")
    val images: List<ImageDto>,

    @SerializedName("im:itemCount")
    val itemCount: LabelDto,

    @SerializedName("im:price")
    val price: PriceDto,

    val rights: LabelDto,

    val title: LabelDto,

    val link: LinkDto,

    val id: IdDto,

    @SerializedName("im:artist")
    val artist: ArtistDto,

    val category: CategoryDto,

    @SerializedName("im:releaseDate")
    val releaseDate: ReleaseDateDto
)

data class LabelDto(
    val label: String
)

data class ImageDto(
    val label: String,
    val attributes: ImageAttributesDto
)

data class ImageAttributesDto(
    val height: String
)

data class PriceDto(
    val label: String,
    val attributes: PriceAttributesDto
)

data class PriceAttributesDto(
    val amount: String,
    val currency: String
)

data class IdDto(
    val label: String,
    val attributes: IdAttributesDto
)

data class IdAttributesDto(
    @SerializedName("im:id")
    val id: String
)

data class ArtistDto(
    val label: String,
    val attributes: ArtistAttributesDto?
)

data class ArtistAttributesDto(
    val href: String
)

data class CategoryDto(
    val attributes: CategoryAttributesDto
)

data class CategoryAttributesDto(
    @SerializedName("im:id")
    val id: String,

    val term: String,
    val label: String
)

data class ReleaseDateDto(
    val label: String,
    val attributes: ReleaseDateAttributesDto
)

data class ReleaseDateAttributesDto(
    val label: String
)

data class LinkDto(
    val attributes: LinkAttributesDto
)

data class LinkAttributesDto(
    val href: String
)