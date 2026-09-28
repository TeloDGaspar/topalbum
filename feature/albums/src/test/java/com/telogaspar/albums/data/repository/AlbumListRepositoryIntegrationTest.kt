package com.telogaspar.albums.data.repository

import com.telogaspar.albums.data.api.TopAlbumsApi
import com.telogaspar.albums.data.mapper.AlbumDetailsMapper
import com.telogaspar.albums.data.mapper.AlbumMapper
import com.telogaspar.albums.data.remote.TopAlbumsListRemoteDataSourceImpl
import com.telogaspar.albums.domain.exception.AlbumException
import com.telogaspar.albums.domain.model.Album
import com.telogaspar.albums.domain.model.AlbumDetails
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import mockwebserver3.SocketEffect
import okhttp3.OkHttpClient
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Runs the real Retrofit + Gson + repository + mappers stack against a local server
 * that serves a trimmed copy of the real iTunes feed (`top_albums.json`).
 */
class AlbumListRepositoryIntegrationTest {

    private val server = MockWebServer()

    private lateinit var repository: AlbumListRepositoryImpl

    @Before
    fun setUp() {
        server.start()

        val client = OkHttpClient.Builder()
            .retryOnConnectionFailure(false)
            .connectTimeout(2, TimeUnit.SECONDS)
            .readTimeout(2, TimeUnit.SECONDS)
            .build()

        val api = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TopAlbumsApi::class.java)

        repository = AlbumListRepositoryImpl(
            remoteDataSource = TopAlbumsListRemoteDataSourceImpl(api),
            albumMapper = AlbumMapper(),
            albumDetailsMapper = AlbumDetailsMapper(),
        )
    }

    @After
    fun tearDown() {
        server.close()
    }

    @Test
    fun `GIVEN real feed response WHEN getTopAlbums is called THEN albums are parsed and mapped in order`() =
        runTest {
            server.enqueue(feedResponse())

            val albums = repository.getTopAlbums()

            assertEquals(
                listOf(
                    Album(
                        id = "6778607425",
                        name = "Silver Sands Marina",
                        artist = "Kenny Chesney",
                        artworkUrl = "https://is1-ssl.mzstatic.com/image/thumb/Music221/v4/d4/66/3e/" +
                            "d4663e69-0e75-f803-ad91-d3fa5701c72d/093624817796.jpg/170x170bb.png",
                    ),
                    Album(
                        id = "6792048044",
                        name = "I'm So Happy",
                        artist = "Ms. Rachel",
                        artworkUrl = "https://is1-ssl.mzstatic.com/image/thumb/Music211/v4/0b/88/df/" +
                            "0b88df94-ec02-412b-a6bc-9ebef710142b/820233141529.jpg/170x170bb.png",
                    ),
                ),
                albums,
            )

            assertEquals(
                "/us/rss/topalbums/limit=100/json",
                server.takeRequest().url.encodedPath,
            )
        }

    @Test
    fun `GIVEN real feed response WHEN getDetailAlbum is called THEN details are parsed without a second request`() =
        runTest {
            server.enqueue(feedResponse())

            repository.getTopAlbums()
            val details = repository.getDetailAlbum("6792048044")

            assertEquals(
                AlbumDetails(
                    id = "6792048044",
                    name = "I'm So Happy",
                    artist = "Ms. Rachel",
                    artworkUrl = "https://is1-ssl.mzstatic.com/image/thumb/Music211/v4/0b/88/df/" +
                        "0b88df94-ec02-412b-a6bc-9ebef710142b/820233141529.jpg/170x170bb.png",
                    genre = "Children's Music",
                    releaseDate = "September 25, 2026",
                    price = "$5.99",
                    trackCount = 23,
                    rights = "℗ 2026 Songs For Littles, LLC",
                    link = "https://music.apple.com/us/album/im-so-happy/6792048044?uo=2",
                ),
                details,
            )

            assertEquals(1, server.requestCount)
        }

    @Test
    fun `GIVEN album id not in feed WHEN getDetailAlbum is called THEN NotFoundException is thrown`() =
        runTest {
            server.enqueue(feedResponse())

            val error = runCatching { repository.getDetailAlbum("999") }.exceptionOrNull()

            assertTrue(error is AlbumException.NotFoundException)
        }

    private fun feedResponse(): MockResponse =
        MockResponse.Builder()
            .code(200)
            .body(readFixture("top_albums.json"))
            .build()

    private fun readFixture(name: String): String =
        checkNotNull(javaClass.classLoader?.getResource(name)) { "Missing test fixture: $name" }
            .readText()
}
