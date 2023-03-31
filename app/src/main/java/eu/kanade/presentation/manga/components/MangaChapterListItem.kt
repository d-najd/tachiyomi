package eu.kanade.presentation.manga.components

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkRemove
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissValue
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eu.kanade.tachiyomi.R
import eu.kanade.tachiyomi.data.download.model.Download
import tachiyomi.presentation.core.components.material.ReadItemAlpha
import tachiyomi.presentation.core.components.material.SecondaryItemAlpha
import tachiyomi.presentation.core.util.selectedBackground

@Composable
fun MangaChapterListItem(
    modifier: Modifier = Modifier,
    title: String,
    date: String?,
    readProgress: String?,
    scanlator: String?,
    read: Boolean,
    bookmark: Boolean,
    selected: Boolean,
    downloadIndicatorEnabled: Boolean,
    downloadStateProvider: () -> Download.State,
    downloadProgressProvider: () -> Int,
    onLongClick: () -> Unit,
    onClick: () -> Unit,
    onDownloadClick: ((ChapterDownloadAction) -> Unit)?,
    onSwipeToBookmark: () -> Unit,
    onSwipeToMarkAsRead: () -> Unit,
) {
    val textAlpha = if (read) ReadItemAlpha else 1f
    val textSubtitleAlpha = if (read) ReadItemAlpha else SecondaryItemAlpha

    // var previousBookmarkState: Boolean? by remember { mutableStateOf(null) }
    // var previousReadState: Boolean? by remember { mutableStateOf(null) }
    val dismissState = rememberDismissState()
    val context = LocalContext.current
    LaunchedEffect(dismissState.currentValue) {
        when (dismissState.currentValue) {
            DismissValue.DismissedToStart -> {
                // previousBookmarkState = bookmark
                onSwipeToBookmark()
                dismissState.reset()
            }
            DismissValue.DismissedToEnd -> {
                // previousReadState = read
                onSwipeToMarkAsRead()
                dismissState.reset()
            }
            DismissValue.Default -> { }
        }
    }
    /*
    if (previousBookmarkState != null && previousBookmarkState == bookmark) {
        LaunchedEffect(Unit) {
            dismissState.reset()
            previousBookmarkState = null
        }
    }
    if (previousReadState != null && previousReadState == read) {
        LaunchedEffect(Unit) {
            dismissState.reset()
            previousReadState = null
        }
    }
     */
    SwipeToDismiss(
        state = dismissState,
        background = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        when (dismissState.dismissDirection) {
                            DismissDirection.StartToEnd -> Color.LightGray
                            DismissDirection.EndToStart -> MaterialTheme.colorScheme.primary
                            null -> Color.Unspecified
                        },
                    ),
            ) {
                Icon(
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .align(Alignment.CenterStart)
                        .alpha(
                            if (dismissState.dismissDirection == DismissDirection.StartToEnd) {
                                1f
                            } else {
                                0f
                            },
                        ),
                    imageVector = if (!bookmark) {
                        Icons.Default.Bookmark
                    } else {
                        Icons.Default.BookmarkRemove
                    },
                    contentDescription = null,
                )
                Icon(
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .align(Alignment.CenterEnd)
                        .alpha(
                            if (dismissState.dismissDirection == DismissDirection.EndToStart) {
                                1f
                            } else {
                                0f
                            },
                        ),
                    imageVector = if (!read) {
                        Icons.Default.Visibility
                    } else {
                        Icons.Default.VisibilityOff
                    },
                    contentDescription = null,
                )
            }
        },
        dismissContent = {
            Row(
                modifier = modifier
                    .selectedBackground(selected)
                    .background(MaterialTheme.colorScheme.background)
                    .combinedClickable(
                        onClick = onClick,
                        onLongClick = onLongClick,
                    )
                    .padding(start = 16.dp, top = 12.dp, end = 8.dp, bottom = 12.dp),
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        var textHeight by remember { mutableStateOf(0) }
                        if (!read) {
                            Icon(
                                imageVector = Icons.Filled.Circle,
                                contentDescription = stringResource(R.string.unread),
                                modifier = Modifier
                                    .height(8.dp)
                                    .padding(end = 4.dp),
                                tint = MaterialTheme.colorScheme.primary,
                            )
                        }
                        if (bookmark) {
                            Icon(
                                imageVector = Icons.Filled.Bookmark,
                                contentDescription = stringResource(R.string.action_filter_bookmarked),
                                modifier = Modifier
                                    .sizeIn(maxHeight = with(LocalDensity.current) { textHeight.toDp() - 2.dp }),
                                tint = MaterialTheme.colorScheme.primary,
                            )
                        }
                        Text(
                            text = title,
                            style = MaterialTheme.typography.bodyMedium,
                            color = LocalContentColor.current.copy(alpha = textAlpha),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            onTextLayout = { textHeight = it.size.height },
                        )
                    }

                    Row {
                        ProvideTextStyle(
                            value = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 12.sp,
                                color = LocalContentColor.current.copy(alpha = textSubtitleAlpha),
                            ),
                        ) {
                            if (date != null) {
                                Text(
                                    text = date,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                if (readProgress != null || scanlator != null) DotSeparatorText()
                            }
                            if (readProgress != null) {
                                Text(
                                    text = readProgress,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.alpha(ReadItemAlpha),
                                )
                                if (scanlator != null) DotSeparatorText()
                            }
                            if (scanlator != null) {
                                Text(
                                    text = scanlator,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        }
                    }
                }

                if (onDownloadClick != null) {
                    ChapterDownloadIndicator(
                        enabled = downloadIndicatorEnabled,
                        modifier = Modifier.padding(start = 4.dp),
                        downloadStateProvider = downloadStateProvider,
                        downloadProgressProvider = downloadProgressProvider,
                        onClick = onDownloadClick,
                    )
                }
            }
        },
    )
}
