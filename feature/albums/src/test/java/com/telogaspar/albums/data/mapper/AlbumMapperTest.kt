package com.telogaspar.albums.data.mapper

import com.telogaspar.albums.data.remote.AlbumDto
import com.telogaspar.albums.data.remote.ArtistDto
import com.telogaspar.albums.data.remote.CategoryAttributesDto
import com.telogaspar.albums.data.remote.CategoryDto
import com.telogaspar.albums.data.remote.IdAttributesDto
import com.telogaspar.albums.data.remote.IdDto
import com.telogaspar.albums.data.remote.ImageAttributesDto
import com.telogaspar.albums.data.remote.ImageDto
import com.telogaspar.albums.data.remote.LabelDto
import com.telogaspar.albums.data.remote.LinkAttributesDto
import com.telogaspar.albums.data.remote.LinkDto
import com.telogaspar.albums.data.remote.PriceAttributesDto
import com.telogaspar.albums.data.remote.PriceDto
import com.telogaspar.albums.data.remote.ReleaseDateAttributesDto
import com.telogaspar.albums.data.remote.ReleaseDateDto
import com.telogaspar.albums.domain.model.Album
import junit.framework.TestCase.assertEquals
import org.junit.Test

class AlbumMapperTest {

    private val mapper = AlbumMapper()

    @Test
    fun `GIVEN valid album responses WHEN map is called THEN albums are mapped correctly`() {
        val source = listOf(
            albumDto(
                id = "1",
                name = "Album One",
                artist = "Artist One",
                imageUrl = "https://image/1.png",
            ),
        )

        val result = mapper.map(source)

        assertEquals(1, result.size)

        val album = result.first()

        assertEquals("1", album.id)
        assertEquals("Album One", album.name)
        assertEquals("Artist One", album.artist)
        assertEquals("https://image/1.png", album.artworkUrl)
    }

    @Test
    fun `GIVEN multiple album responses WHEN map is called THEN order is preserved`() {
        val source = listOf(
            albumDto(id = "1", name = "First"),
            albumDto(id = "2", name = "Second"),
        )

        val result = mapper.map(source)

        assertEquals(listOf("1", "2"), result.map { it.id })
        assertEquals(listOf("First", "Second"), result.map { it.name })
    }

    @Test
    fun `GIVEN empty source WHEN map is called THEN empty list is returned`() {
        val result = mapper.map(emptyList())

        assertEquals(emptyList<Album>(), result)
    }
}

fun albumDto(
    id: String = "1",
    name: String = "Album",
    artist: String = "Artist",
    imageUrl: String = "https://image.png",
    genre: String = "Pop",
    releaseDate: String = "September 25, 2026",
    price: String = "$9.99",
    trackCount: String = "10",
    rights: String = "Copyright",
    link: String = "https://music.apple.com/album/1",
): AlbumDto =
    AlbumDto(
        name = LabelDto(
            label = name,
        ),
        images = listOf(
            ImageDto(
                label = imageUrl,
                attributes = ImageAttributesDto(
                    height = "170",
                ),
            ),
        ),
        itemCount = LabelDto(
            label = trackCount,
        ),
        price = PriceDto(
            label = price,
            attributes = PriceAttributesDto(
                amount = price.removePrefix("$"),
                currency = "USD",
            ),
        ),
        rights = LabelDto(
            label = rights,
        ),
        title = LabelDto(
            label = "$name - $artist",
        ),
        link = LinkDto(
            attributes = LinkAttributesDto(
                href = link,
            ),
        ),
        id = IdDto(
            label = link,
            attributes = IdAttributesDto(
                id = id,
            ),
        ),
        artist = ArtistDto(
            label = artist,
            attributes = null,
        ),
        category = CategoryDto(
            attributes = CategoryAttributesDto(
                id = "14",
                term = genre,
                label = genre,
            ),
        ),
        releaseDate = ReleaseDateDto(
            label = "2026-09-25T00:00:00-07:00",
            attributes = ReleaseDateAttributesDto(
                label = releaseDate,
            ),
        ),
    )