package com.telogaspar.albums.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.telogaspar.albums.R
import com.telogaspar.albums.domain.model.Album
import com.telogaspar.albums.presentation.components.EmptyState
import com.telogaspar.albums.presentation.components.ErrorState
import com.telogaspar.albums.presentation.components.LoadingState

@Composable
fun AlbumListScreen(
    viewModel: AlbumListViewModel = hiltViewModel(),
    onAlbumClick: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AlbumListScreen(
        uiState = uiState,
        onRetry = viewModel::retry,
        onAlbumClick = onAlbumClick,
    )
}

@Composable
internal fun AlbumListScreen(
    uiState: AlbumListUiState,
    onRetry: () -> Unit,
    onAlbumClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        AlbumListAppBar()

        when (uiState) {
            AlbumListUiState.Loading -> {
                LoadingState()
            }

            AlbumListUiState.Empty -> {
                EmptyState(
                    message = "No albums available",
                )
            }

            is AlbumListUiState.Error -> {
                ErrorState(
                    title = "Unable to load albums",
                    message = uiState.type.toMessage(),
                    actionLabel = "Retry",
                    onAction = onRetry,
                )
            }

            is AlbumListUiState.Success -> {
                AlbumListColumn(
                    albums = uiState.albums,
                    listState = listState,
                    onAlbumClick = onAlbumClick,
                )
            }
        }
    }
}

@Composable
internal fun AlbumListColumn(
    albums: List<Album>,
    listState: LazyListState,
    onAlbumClick: (String) -> Unit,
) {
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            horizontal = 16.dp,
            vertical = 12.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(
            items = albums,
            key = { album -> album.id },
        ) { album ->
            AlbumRowCard(
                album = album,
                onClick = {
                    onAlbumClick(album.id)
                },
            )
        }
    }
}

@Composable
internal fun AlbumRowCard(
    album: Album,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                model = album.artworkUrl,
                contentDescription = "${album.name} artwork",
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.ic_album_placeholder),
                error = painterResource(R.drawable.ic_album_placeholder),
            )

            Spacer(
                modifier = Modifier.width(16.dp),
            )

            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = album.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )

                Spacer(
                    modifier = Modifier.height(4.dp),
                )

                Text(
                    text = album.artist,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AlbumListAppBar() {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = "Top Albums",
                    style = MaterialTheme.typography.titleLarge,
                )

                Text(
                    text = "iTunes Top 100",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
    )
}
