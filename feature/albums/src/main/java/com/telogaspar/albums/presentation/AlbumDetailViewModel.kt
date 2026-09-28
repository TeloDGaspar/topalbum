package com.telogaspar.albums.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.telogaspar.albums.domain.model.AlbumDetails
import com.telogaspar.albums.domain.repository.AlbumListRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
    private val repository: AlbumListRepository,
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<AlbumDetailUiState>(AlbumDetailUiState.Loading)

    val uiState = _uiState.asStateFlow()

    private var albumId: String? = null

    private var loadJob: Job? = null

    fun loadAlbum(albumId: String) {
        this.albumId = albumId

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

    fun retry() {
        albumId?.let(::loadAlbum)
    }
}