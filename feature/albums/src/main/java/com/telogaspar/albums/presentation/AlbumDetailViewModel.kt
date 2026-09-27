package com.telogaspar.albums.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.telogaspar.albums.domain.model.AlbumDetails
import com.telogaspar.albums.domain.repository.AlbumListRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface AlbumDetailUiState {

    data object Loading : AlbumDetailUiState

    data class Success(
        val details: AlbumDetails,
    ) : AlbumDetailUiState

    data class Error(
        val message: String,
    ) : AlbumDetailUiState
}

@HiltViewModel
class AlbumDetailViewModel @Inject constructor(
    private val repository: AlbumListRepository,
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<AlbumDetailUiState>(AlbumDetailUiState.Loading)

    val uiState = _uiState.asStateFlow()

    private var albumId: String? = null

    fun loadAlbum(albumId: String) {
        this.albumId = albumId

        viewModelScope.launch {
            _uiState.value = AlbumDetailUiState.Loading

            runCatching {
                repository.getDetailAlbum(albumId)
            }.onSuccess { album ->
                _uiState.value = AlbumDetailUiState.Success(album)
            }.onFailure { throwable ->
                _uiState.value = AlbumDetailUiState.Error(
                    throwable.message ?: "Unable to load album details"
                )
            }
        }
    }

    fun retry() {
        albumId?.let(::loadAlbum)
    }
}