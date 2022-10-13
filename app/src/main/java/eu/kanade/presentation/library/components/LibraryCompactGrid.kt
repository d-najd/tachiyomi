package eu.kanade.presentation.library.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eu.kanade.domain.library.model.LibraryManga
import eu.kanade.tachiyomi.R
import eu.kanade.tachiyomi.ui.library.LibraryItem

@Composable
fun LibraryCompactGrid(
    items: List<LibraryItem>,
    columns: Int,
    contentPadding: PaddingValues,
    selection: List<LibraryManga>,
    onClick: (LibraryManga) -> Unit,
    onLongClick: (LibraryManga) -> Unit,
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
            contentType = { "library_compact_grid_item" },
        ) { libraryItem ->
            LibraryCompactGridItem(
                item = libraryItem,
                isSelected = libraryItem.libraryManga in selection,
                onClick = onClick,
                onLongClick = onLongClick,
            )
        }
    }
}

@Composable
fun LibraryCompactGridItem(
    item: LibraryItem,
    isSelected: Boolean,
    onClick: (LibraryManga) -> Unit,
    onLongClick: (LibraryManga) -> Unit,
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
        downloadCount = item.downloadCount,
        unreadCount = item.unreadCount,
        isLocal = item.isLocal,
        language = item.sourceLanguage,
    ) {
        // TODO FOUND IT
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp))
                .background(
                    Brush.verticalGradient(
                        0f to Color.Transparent,
                        1f to Color(0xAA000000),
                    ),
                )
                .fillMaxHeight(0.33f)
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
        )
        MangaGridCompactText(manga.title)

        MangaReturnReadingButton()
    }
}

// TODO finish this
// https://stackoverflow.com/questions/56767624/how-to-load-image-from-drawable-in-jetpack-compose

//https://stackoverflow.com/questions/70708107/how-to-make-text-centered-vertically-in-android-compose
@Composable
fun BoxScope.MangaReturnReadingButton() {
    Card(
        border = BorderStroke(2.dp, Color.Red),
        modifier = Modifier
            .padding(8.dp)
            .size(32.dp)
            .align(Alignment.BottomEnd)
            .clickable {
            },
    ) {
        Image(
            painterResource(R.drawable.ic_continue_reading_24dp),
            contentDescription = "",
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                //.absoluteOffset(x = 2.dp, y = 2.dp)
                .size(24.dp),
            verticalAlignment = Alignment.CenterVertically,
        )
    }

    /*
    ElevatedCard(
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onRename() }
                .padding(start = horizontalPadding, top = horizontalPadding, end = horizontalPadding),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(imageVector = Icons.Outlined.Label, contentDescription = "")
            Text(
                text = category.name,
                modifier = Modifier
                    .padding(start = horizontalPadding),
            )
        }

     */
}

@Composable
fun BoxScope.MangaGridCompactText(
    text: String,
) {
    Text(
        text = text,
        modifier = Modifier
            .padding(start = 8.dp, top = 8.dp, bottom = 8.dp, end = 34.dp)
            .align(Alignment.BottomStart),
        color = Color.White,
        fontSize = 12.sp,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        style = MaterialTheme.typography.titleSmall.copy(
            shadow = Shadow(
                color = Color.Black,
                blurRadius = 4f,
            ),
        ),
    )
}
