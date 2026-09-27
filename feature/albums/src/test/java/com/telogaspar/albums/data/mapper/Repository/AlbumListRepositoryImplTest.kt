package com.telogaspar.albums.data.mapper.Repository

import com.telogaspar.albums.data.mapper.AlbumDetailsMapper
import com.telogaspar.albums.data.mapper.AlbumMapper
import com.telogaspar.albums.data.mapper.albumDto
import com.telogaspar.albums.data.remote.AlbumDto
import com.telogaspar.albums.data.remote.FeedDto
import com.telogaspar.albums.data.remote.TopAlbumsListRemoteDataSource
import com.telogaspar.albums.data.remote.TopAlbumsResponseDto
import com.telogaspar.albums.data.repository.AlbumListRepositoryImpl
import com.telogaspar.albums.domain.model.Album
import com.telogaspar.albums.domain.model.AlbumDetails
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class AlbumListRepositoryImplTest {

    private val remoteDataSource = mockk<TopAlbumsListRemoteDataSource>()
    private val albumMapper = mockk<AlbumMapper>()
    private val albumDetailsMapper = mockk<AlbumDetailsMapper>()

    private lateinit var repository: AlbumListRepositoryImpl

    @Before
    fun setup() {
        repository = AlbumListRepositoryImpl(
            remoteDataSource = remoteDataSource,
            albumMapper = albumMapper,
            albumDetailsMapper = albumDetailsMapper,
        )
    }

    @Test
    fun `GIVEN albums from remote WHEN getTopAlbums is called THEN mapped albums are returned`() =
        runTest {
            val dto = albumDto(id = "1")
            val response = topAlbumsResponse(
                entries = listOf(dto),
            )

            val expected = listOf(
                Album(
                    id = "1",
                    name = "Album",
                    artist = "Artist",
                    artworkUrl = "https://image.png",
                ),
            )

            coEvery {
                remoteDataSource.fetchAlbumList()
            } returns response

            every {
                albumMapper.map(listOf(dto))
            } returns expected

            val result = repository.getTopAlbums()

            assertEquals(expected, result)

            coVerify(exactly = 1) {
                remoteDataSource.fetchAlbumList()
            }
        }

    @Test
    fun `GIVEN albums already fetched WHEN detail is requested THEN api is not called again`() =
        runTest {
            val dto = albumDto(
                id = "123",
                name = "Album",
                artist = "Artist",
                imageUrl = "https://image.png",
                genre = "Pop",
                releaseDate = "September 25, 2026",
                price = "$9.99",
                trackCount = "10",
                rights = "Copyright",
                link = "https://music.apple.com/album/123",
            )

            val response = topAlbumsResponse(
                entries = listOf(dto),
            )

            val albums = listOf(
                Album(
                    id = "123",
                    name = "Album",
                    artist = "Artist",
                    artworkUrl = "https://image.png",
                ),
            )

            val details = AlbumDetails(
                id = "123",
                name = "Album",
                artist = "Artist",
                artworkUrl = "https://image.png",
                genre = "Pop",
                releaseDate = "September 25, 2026",
                price = "$9.99",
                trackCount = 10,
                rights = "Copyright",
                link = "https://music.apple.com/album/123",
            )

            coEvery {
                remoteDataSource.fetchAlbumList()
            } returns response

            every {
                albumMapper.map(any())
            } returns albums

            every {
                albumDetailsMapper.map(dto)
            } returns details

            repository.getTopAlbums()

            val result = repository.getDetailAlbum("123")

            assertEquals(details, result)

            coVerify(exactly = 1) {
                remoteDataSource.fetchAlbumList()
            }
        }

    private fun topAlbumsResponse(
        entries: List<AlbumDto>,
    ): TopAlbumsResponseDto =
        TopAlbumsResponseDto(
            feed = FeedDto(
                entry = entries,
            ),
        )
}