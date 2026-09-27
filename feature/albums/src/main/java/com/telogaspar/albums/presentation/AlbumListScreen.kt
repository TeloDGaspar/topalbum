package com.telogaspar.albums.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.telogaspar.albums.domain.model.Album

@Composable
fun AlbumListScreen(
    viewModel: AlbumListViewModel = hiltViewModel(),
    onAlbumClick: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        AlbumListAppBar()

        when (val state = uiState) {
            AlbumListUiState.Loading -> {
                AlbumListLoadingState()
            }

            AlbumListUiState.Empty -> {
                AlbumListEmptyState()
            }

            is AlbumListUiState.Error -> {
                AlbumListErrorState(
                    message = state.message,
                    onRetry = viewModel::retry,
                )
            }

            is AlbumListUiState.Success -> {
                AlbumListColumn(
                    albums = state.albums,
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
//            AsyncImage(
//                model = album.artworkUrl,
//                contentDescription = "${album.name} artwork",
//                modifier = Modifier
//                    .size(80.dp)
//                    .clip(RoundedCornerShape(12.dp)),
//                contentScale = ContentScale.Crop,
//            )

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

//            Icon(
//                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
//                contentDescription = null,
//            )
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

@Composable
internal fun AlbumListLoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
internal fun AlbumListErrorState(
    message: String,
    onRetry: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Unable to load albums",
            style = MaterialTheme.typography.titleMedium,
        )

        Spacer(
            modifier = Modifier.height(8.dp),
        )

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )

        Spacer(
            modifier = Modifier.height(16.dp),
        )

        Button(
            onClick = onRetry,
        ) {
            Text("Retry")
        }
    }
}

@Composable
internal fun AlbumListEmptyState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "No albums available",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}