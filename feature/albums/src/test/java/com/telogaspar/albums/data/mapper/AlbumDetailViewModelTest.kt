package com.telogaspar.albums.data.mapper

import com.telogaspar.albums.domain.exception.AlbumException
import com.telogaspar.albums.domain.model.AlbumDetails
import com.telogaspar.albums.domain.repository.AlbumListRepository
import com.telogaspar.albums.presentation.AlbumDetailUiState
import com.telogaspar.albums.presentation.AlbumDetailViewModel
import com.telogaspar.albums.presentation.ErrorType
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class AlbumDetailViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val repository: AlbumListRepository = mockk()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `GIVEN repository returns album details WHEN album is loaded THEN success state contains album details`() =
        runTest {
            val details = albumDetails(
                id = "123",
            )

            coEvery {
                repository.getDetailAlbum("123")
            } returns details

            val viewModel = AlbumDetailViewModel(
                repository = repository,
            )

            viewModel.loadAlbum("123")

            advanceUntilIdle()

            assertEquals(
                AlbumDetailUiState.Success(details),
                viewModel.uiState.value,
            )
        }

    @Test
    fun `GIVEN repository throws api exception WHEN loading album details THEN server error state is shown`() =
        runTest {
            coEvery {
                repository.getDetailAlbum("123")
            } throws AlbumException.ApiException(code = 500, cause = RuntimeException())

            val viewModel = AlbumDetailViewModel(
                repository = repository,
            )

            viewModel.loadAlbum("123")

            advanceUntilIdle()

            assertEquals(
                AlbumDetailUiState.Error(ErrorType.Server),
                viewModel.uiState.value,
            )
        }

    @Test
    fun `GIVEN error state WHEN retry is called THEN album details are reloaded`() =
        runTest {
            val details = albumDetails(
                id = "123",
            )

            coEvery {
                repository.getDetailAlbum("123")
            } throws AlbumException.NetworkException(IOException()) andThen details

            val viewModel = AlbumDetailViewModel(
                repository = repository,
            )

            viewModel.loadAlbum("123")

            advanceUntilIdle()

            assertEquals(
                AlbumDetailUiState.Error(ErrorType.Network),
                viewModel.uiState.value,
            )

            viewModel.retry()

            advanceUntilIdle()

            assertEquals(
                AlbumDetailUiState.Success(details),
                viewModel.uiState.value,
            )
        }

    @Test
    fun `GIVEN album id does not exist WHEN album is loaded THEN error state is shown`() =
        runTest {
            coEvery {
                repository.getDetailAlbum("999")
            } throws AlbumException.NotFoundException("999")

            val viewModel = AlbumDetailViewModel(repository)

            viewModel.loadAlbum("999")

            advanceUntilIdle()

            assertEquals(
                AlbumDetailUiState.Error(ErrorType.NotFound),
                viewModel.uiState.value,
            )
        }

    private fun albumDetails(
        id: String,
        name: String = "Album $id",
        artist: String = "Artist $id",
    ): AlbumDetails =
        AlbumDetails(
            id = id,
            name = name,
            artist = artist,
            artworkUrl = "https://image/$id.png",
            genre = "Pop",
            releaseDate = "September 25, 2026",
            price = "$9.99",
            trackCount = 10,
            rights = "Copyright",
            link = "https://music.apple.com/album/$id",
        )
}