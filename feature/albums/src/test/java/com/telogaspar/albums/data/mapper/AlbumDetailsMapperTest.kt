package com.telogaspar.albums.data.mapper

import junit.framework.TestCase.assertEquals
import org.junit.Test

class AlbumDetailsMapperTest {

    private val mapper = AlbumDetailsMapper()

    @Test
    fun `GIVEN valid album response WHEN map is called THEN album details are mapped correctly`() {
        val source = albumDto(
            genre = "Pop",
            releaseDate = "September 25, 2026",
            price = "$9.99",
            trackCount = "12",
            rights = "Copyright label",
            link = "https://music.apple.com/album/1",
        )

        val result = mapper.map(source)

        assertEquals("Pop", result.genre)
        assertEquals("September 25, 2026", result.releaseDate)
        assertEquals("$9.99", result.price)
        assertEquals(12, result.trackCount)
        assertEquals("Copyright label", result.rights)
        assertEquals("https://music.apple.com/album/1", result.link)
    }

    @Test
    fun `GIVEN invalid track count WHEN map is called THEN track count defaults to zero`() {
        val source = albumDto(
            trackCount = "invalid",
        )

        val result = mapper.map(source)

        assertEquals(0, result.trackCount)
    }
}