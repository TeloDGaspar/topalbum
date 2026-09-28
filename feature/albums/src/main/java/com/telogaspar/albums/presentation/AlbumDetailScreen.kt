package com.telogaspar.albums.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.telogaspar.albums.R
import com.telogaspar.albums.domain.model.AlbumDetails
import com.telogaspar.albums.presentation.components.ErrorState
import com.telogaspar.albums.presentation.components.LoadingState

@Composable
fun AlbumDetailScreen(
    viewModel: AlbumDetailViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AlbumDetailScreen(
        uiState = uiState,
        onRetry = viewModel::retry,
        onBackClick = onBackClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AlbumDetailScreen(
    uiState: AlbumDetailUiState,
    onRetry: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text("Album details")
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
            )
        },
    ) { paddingValues ->
        when (uiState) {
            AlbumDetailUiState.Loading -> {
                LoadingState(
                    modifier = Modifier.padding(paddingValues),
                )
            }

            is AlbumDetailUiState.Error -> {
                val isRetryable = uiState.type.isRetryable

                ErrorState(
                    title = "Unable to load album",
                    message = uiState.type.toMessage(),
                    actionLabel = if (isRetryable) "Retry" else "Go back",
                    onAction = if (isRetryable) onRetry else onBackClick,
                    modifier = Modifier.padding(paddingValues),
                )
            }

            is AlbumDetailUiState.Success -> {
                AlbumDetailContent(
                    album = uiState.details,
                    modifier = Modifier.padding(paddingValues),
                )
            }
        }
    }
}

@Composable
internal fun AlbumDetailContent(
    album: AlbumDetails,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AsyncImage(
            model = album.artworkUrl,
            contentDescription = "${album.name} artwork",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(220.dp)
                .clip(RoundedCornerShape(20.dp)),
            placeholder = painterResource(R.drawable.ic_album_placeholder),
            error = painterResource(R.drawable.ic_album_placeholder),
        )

        Spacer(
            modifier = Modifier.height(24.dp),
        )

        Text(
            text = album.name,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
        )

        Spacer(
            modifier = Modifier.height(8.dp),
        )

        Text(
            text = album.artist,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )

        Spacer(
            modifier = Modifier.height(28.dp),
        )

        AlbumDetailRow(
            label = "Genre",
            value = album.genre,
        )

        AlbumDetailRow(
            label = "Release date",
            value = album.releaseDate,
        )

        AlbumDetailRow(
            label = "Price",
            value = album.price,
        )

        AlbumDetailRow(
            label = "Tracks",
            value = album.trackCount.toString(),
        )

        Spacer(
            modifier = Modifier.height(24.dp),
        )

        Text(
            text = album.rights,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
internal fun AlbumDetailRow(
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.End,
        )
    }
}