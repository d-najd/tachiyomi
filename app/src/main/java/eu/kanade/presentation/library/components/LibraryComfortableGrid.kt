package eu.kanade.presentation.library.components

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eu.kanade.domain.library.model.LibraryManga
import eu.kanade.domain.manga.model.MangaCover
import eu.kanade.tachiyomi.ui.library.LibraryItem

@Composable
fun LibraryComfortableGrid(
    items: List<LibraryItem>,
    columns: Int,
    contentPadding: PaddingValues,
    selection: List<LibraryManga>,
    onClick: (LibraryManga) -> Unit,
    onLongClick: (LibraryManga) -> Unit,
    onClickLastRead: (Long) -> Unit,
    searchQuery: String?,
    onGlobalSearchClicked: () -> Unit,
) {
    LazyLibraryGrid(
        modifier = Modifier.fillMaxSize(),
        columns = columns,
        contentPadding = contentPadding,
    ) {
        globalSearchItem(searchQuery, onGlobalSearchClicked)

        items(
            items = items,
            contentType = { "library_comfortable_grid_item" },
        ) { libraryItem ->
            LibraryComfortableGridItem(
                libraryItem,
                libraryItem.libraryManga in selection,
                onClick,
                onLongClick,
                onClickLastRead,
            )
        }
    }
}

@Composable
fun LibraryComfortableGridItem(
    item: LibraryItem,
    isSelected: Boolean,
    onClick: (LibraryManga) -> Unit,
    onLongClick: (LibraryManga) -> Unit,
    onClickLastRead: (Long) -> Unit,
) {
    val libraryManga = item.libraryManga
    val manga = libraryManga.manga
    LibraryGridItemSelectable(isSelected = isSelected) {
        Column(
            modifier = Modifier
                .combinedClickable(
                    onClick = {
                        onClick(libraryManga)
                    },
                    onLongClick = {
                        onLongClick(libraryManga)
                    },
                ),
        ) {
            LibraryGridCover(
                mangaCover = MangaCover(
                    manga.id,
                    manga.source,
                    manga.favorite,
                    manga.thumbnailUrl,
                    manga.coverLastModified,
                ),
                downloadCount = item.downloadCount,
                unreadCount = item.unreadCount,
                isLocal = item.isLocal,
                language = item.sourceLanguage,
            ) {
                LibraryGridItemLastReadButton(manga.id, onClickLastRead)
            }
            MangaGridComfortableText(
                text = manga.title,
            )
        }
    }
}

@Composable
fun MangaGridComfortableText(
    text: String,
) {
    Text(
        modifier = Modifier.padding(4.dp),
        text = text,
        fontSize = 12.sp,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        style = MaterialTheme.typography.titleSmall,
    )
}
