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

sealed interface AlbumListUiState {

    data object Loading : AlbumListUiState

    data object Empty : AlbumListUiState
    data class Success(
        val albums: List<Album>,
    ) : AlbumListUiState

    data class Error(
        val message: String,
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

    fun loadAlbums() {
        viewModelScope.launch {
            _uiState.value = AlbumListUiState.Loading

            runCatching {
                repository.getTopAlbums()
            }.onSuccess { albums ->
                _uiState.value = AlbumListUiState.Success(
                    albums = albums,
                )
            }.onFailure { throwable ->
                _uiState.value = AlbumListUiState.Error(
                    message = throwable.message
                        ?: "Something went wrong",
                )
            }
        }
    }

    fun retry() {
        loadAlbums()
    }
}