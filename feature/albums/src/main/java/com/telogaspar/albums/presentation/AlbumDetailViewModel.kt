package com.telogaspar.albums.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telogaspar.albums.domain.model.AlbumDetails
import com.telogaspar.albums.domain.repository.AlbumListRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

sealed interface AlbumDetailUiState {

    data object Loading : AlbumDetailUiState

    data class Success(
        val details: AlbumDetails,
    ) : AlbumDetailUiState

    data class Error(
        val type: ErrorType,
    ) : AlbumDetailUiState
}

@HiltViewModel
class AlbumDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: AlbumListRepository,
) : ViewModel() {

    private val albumId: String = checkNotNull(savedStateHandle[AlbumDetailRoute.ALBUM_ID_KEY])

    private val _uiState =
        MutableStateFlow<AlbumDetailUiState>(AlbumDetailUiState.Loading)

    val uiState = _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        loadAlbum()
    }

    fun retry() {
        loadAlbum()
    }

    private fun loadAlbum() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.value = AlbumDetailUiState.Loading
            try {
                val album = repository.getDetailAlbum(albumId)
                _uiState.value = AlbumDetailUiState.Success(album)
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _uiState.value = AlbumDetailUiState.Error(
                    type = exception.toErrorType(),
                )
            }
        }
    }
}