package com.telogaspar.albums.data.mapper

import com.telogaspar.albums.domain.exception.AlbumException
import com.telogaspar.albums.domain.model.Album
import com.telogaspar.albums.domain.repository.AlbumListRepository
import com.telogaspar.albums.presentation.AlbumListUiState
import com.telogaspar.albums.presentation.AlbumListViewModel
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
class AlbumListViewModelTest {

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
    fun `GIVEN repository returns albums WHEN viewModel is created THEN success state contains albums`() =
        runTest {
            val albums = listOf(
                album("1"),
                album("2"),
            )

            coEvery {
                repository.getTopAlbums()
            } returns albums

            val viewModel = AlbumListViewModel(repository)

            advanceUntilIdle()

            assertEquals(
                AlbumListUiState.Success(albums),
                viewModel.uiState.value,
            )
        }

    @Test
    fun `GIVEN repository returns empty list WHEN viewModel is created THEN empty state is shown`() =
        runTest {
            coEvery {
                repository.getTopAlbums()
            } returns emptyList()

            val viewModel = AlbumListViewModel(repository)

            advanceUntilIdle()

            assertEquals(
                AlbumListUiState.Empty,
                viewModel.uiState.value,
            )
        }

    @Test
    fun `GIVEN repository throws network exception WHEN loading albums THEN network error state is shown`() =
        runTest {
            coEvery {
                repository.getTopAlbums()
            } throws AlbumException.NetworkException(IOException())

            val viewModel = AlbumListViewModel(repository)

            advanceUntilIdle()

            assertEquals(
                AlbumListUiState.Error(ErrorType.Network),
                viewModel.uiState.value,
            )
        }

    @Test
    fun `GIVEN repository throws unexpected exception WHEN loading albums THEN unknown error state is shown`() =
        runTest {
            coEvery {
                repository.getTopAlbums()
            } throws RuntimeException("boom")

            val viewModel = AlbumListViewModel(repository)

            advanceUntilIdle()

            assertEquals(
                AlbumListUiState.Error(ErrorType.Unknown),
                viewModel.uiState.value,
            )
        }

    @Test
    fun `GIVEN error state WHEN retry is called THEN albums are reloaded`() =
        runTest {
            val albums = listOf(album("1"))

            coEvery {
                repository.getTopAlbums()
            } throws AlbumException.NetworkException(IOException()) andThen albums

            val viewModel = AlbumListViewModel(repository)

            advanceUntilIdle()

            assertEquals(
                AlbumListUiState.Error(ErrorType.Network),
                viewModel.uiState.value,
            )

            viewModel.retry()

            advanceUntilIdle()

            assertEquals(
                AlbumListUiState.Success(albums),
                viewModel.uiState.value,
            )
        }

    private fun album(
        id: String,
        name: String = "Album $id",
        artist: String = "Artist $id",
    ) = Album(
        id = id,
        name = name,
        artist = artist,
        artworkUrl = "https://image/$id.png",
    )
}