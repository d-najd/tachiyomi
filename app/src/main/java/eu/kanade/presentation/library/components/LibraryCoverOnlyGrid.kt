package eu.kanade.presentation.library.components

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import eu.kanade.domain.library.model.LibraryManga
import eu.kanade.tachiyomi.ui.library.LibraryItem

@Composable
fun LibraryCoverOnlyGrid(
    items: List<LibraryItem>,
    showDownloadBadges: Boolean,
    showUnreadBadges: Boolean,
    showLocalBadges: Boolean,
    showLanguageBadges: Boolean,
    showContinueReadingButton: Boolean,
    columns: Int,
    contentPadding: PaddingValues,
    selection: List<LibraryManga>,
    onClick: (LibraryManga) -> Unit,
    onLongClick: (LibraryManga) -> Unit,
    onClickContinueReading: (Long) -> Unit,
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
            contentType = { "library_only_cover_grid_item" },
        ) { libraryItem ->
            LibraryCoverOnlyGridItem(
                item = libraryItem,
                showDownloadBadge = showDownloadBadges,
                showUnreadBadge = showUnreadBadges,
                showLocalBadge = showLocalBadges,
                showLanguageBadge = showLanguageBadges,
                showContinueReadingButton = showContinueReadingButton,
                isSelected = libraryItem.libraryManga in selection,
                onClick = onClick,
                onLongClick = onLongClick,
                onClickContinueReading = onClickContinueReading,
            )
        }
    }
}

@Composable
fun LibraryCoverOnlyGridItem(
    item: LibraryItem,
    showDownloadBadge: Boolean,
    showUnreadBadge: Boolean,
    showLocalBadge: Boolean,
    showLanguageBadge: Boolean,
    showContinueReadingButton: Boolean,
    isSelected: Boolean,
    onClick: (LibraryManga) -> Unit,
    onLongClick: (LibraryManga) -> Unit,
    onClickContinueReading: (Long) -> Unit,
) {
    val libraryManga = item.libraryManga
    val manga = libraryManga.manga
    LibraryGridCover(
        modifier = Modifier
            .selectedOutline(isSelected)
            .combinedClickable(
                onClick = {
                    onClick(libraryManga)
                },
                onLongClick = {
                    onLongClick(libraryManga)
                },
            ),
        mangaCover = eu.kanade.domain.manga.model.MangaCover(
            manga.id,
            manga.source,
            manga.favorite,
            manga.thumbnailUrl,
            manga.coverLastModified,
        ),
        item = item,
        showDownloadBadge = showDownloadBadge,
        showUnreadBadge = showUnreadBadge,
        showLocalBadge = showLocalBadge,
        showLanguageBadge = showLanguageBadge,
    ) {
        if (showContinueReadingButton) {
            LibraryGridItemContinueReadingButton(manga.id, onClickContinueReading)
        }
    }
}
