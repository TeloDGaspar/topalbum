package com.telogaspar.albums.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telogaspar.albums.domain.model.Album
import com.telogaspar.albums.domain.repository.AlbumListRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

sealed interface AlbumListUiState {

    data object Loading : AlbumListUiState

    data object Empty : AlbumListUiState
    data class Success(
        val albums: List<Album>,
    ) : AlbumListUiState

    data class Error(
        val type: ErrorType,
    ) : AlbumListUiState
}

@HiltViewModel
class AlbumListViewModel@Inject constructor(
    private val repository: AlbumListRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<AlbumListUiState>(AlbumListUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadAlbums()
    }

    private fun loadAlbums() {
        viewModelScope.launch {
            _uiState.value = AlbumListUiState.Loading
            try {
                val albums = repository.getTopAlbums()

                _uiState.value =
                    if (albums.isEmpty()) {
                        AlbumListUiState.Empty
                    } else {
                        AlbumListUiState.Success(albums)
                    }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _uiState.value = AlbumListUiState.Error(
                    type = exception.toErrorType()
                )
            }
        }
    }

    fun retry() {
        loadAlbums()
    }
}