package eu.kanade.presentation.manga.components

import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissState
import androidx.compose.material.DismissValue
import androidx.compose.material.FixedThreshold
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.MarkChatRead
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eu.kanade.presentation.components.ChapterDownloadAction
import eu.kanade.presentation.components.ChapterDownloadIndicator
import eu.kanade.presentation.util.ReadItemAlpha
import eu.kanade.presentation.util.SecondaryItemAlpha
import eu.kanade.presentation.util.selectedBackground
import eu.kanade.tachiyomi.R
import eu.kanade.tachiyomi.data.download.model.Download
import eu.kanade.tachiyomi.util.lang.launchUI

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
    onSwipeToBookmark: (DismissState) -> Unit,
    onSwipeToMarkAsRead: (DismissState) -> Unit,
) {
    val scope = rememberCoroutineScope()

    val state = rememberDismissState(DismissValue.Default)

    if (state.isDismissed(DismissDirection.StartToEnd)) {
        scope.launchUI {
            state.snapTo(DismissValue.Default)
            onSwipeToMarkAsRead(state)
        }
    }

    if (state.isDismissed(DismissDirection.EndToStart)) {
        scope.launchUI {
            state.snapTo(DismissValue.Default)
            onSwipeToBookmark(state)
        }
    }

    SwipeToDismiss(
        state = state,
        dismissThresholds = { FixedThreshold(64.dp) },
        background = { MangaChapterListItemBackground(state = state) },
        dismissContent = {
            MangaChapterListItemContent(
                modifier = modifier,
                title = title,
                date = date,
                readProgress = readProgress,
                scanlator = scanlator,
                read = read,
                bookmark = bookmark,
                state = state,
                selected = selected,
                downloadIndicatorEnabled = downloadIndicatorEnabled,
                downloadStateProvider = downloadStateProvider,
                downloadProgressProvider = downloadProgressProvider,
                onLongClick = onLongClick,
                onClick = onClick,
                onDownloadClick = onDownloadClick,
            )
        },
        directions = setOf(DismissDirection.StartToEnd, DismissDirection.EndToStart),
    )
}

@Composable
private fun MangaChapterListItemContent(
    modifier: Modifier = Modifier,
    title: String,
    date: String?,
    readProgress: String?,
    scanlator: String?,
    read: Boolean,
    bookmark: Boolean,
    selected: Boolean,
    state: DismissState,
    downloadIndicatorEnabled: Boolean,
    downloadStateProvider: () -> Download.State,
    downloadProgressProvider: () -> Int,
    onLongClick: () -> Unit,
    onClick: () -> Unit,
    onDownloadClick: ((ChapterDownloadAction) -> Unit)?,
) {
    var appearAlpha by remember { mutableStateOf(1f) }

    LaunchedEffect(state.dismissDirection) {
        when (state.dismissDirection) {
            DismissDirection.StartToEnd -> {
                //if(DismissValue != DismissValue.Default)
                animate(0f, 1f, animationSpec = tween(1500)) { value, _ -> appearAlpha = value }
            }
            DismissDirection.EndToStart -> {
                animate(0f, 1f, animationSpec = tween(1500)) { value, _ -> appearAlpha = value }
            }
            else -> {
            }
        }
    }

    Row(
        modifier = modifier
            .selectedBackground(selected)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick,
            )
            .alpha(appearAlpha)
            .background(MaterialTheme.colorScheme.background)
            .padding(start = 16.dp, top = 12.dp, end = 8.dp, bottom = 12.dp),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            val textColor = if (bookmark && !read) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurface
            }
            val textAlpha = remember(read) { if (read) ReadItemAlpha else 1f }
            val textSubtitleAlpha =
                remember(read) { if (read) ReadItemAlpha else SecondaryItemAlpha }

            Row(verticalAlignment = Alignment.CenterVertically) {
                var textHeight by remember { mutableStateOf(0) }
                if (bookmark) {
                    Icon(
                        imageVector = Icons.Filled.Bookmark,
                        contentDescription = stringResource(R.string.action_filter_bookmarked),
                        modifier = Modifier
                            .sizeIn(maxHeight = with(LocalDensity.current) { textHeight.toDp() - 2.dp }),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                }
                Text(
                    text = title,
                    color = textColor,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    onTextLayout = { textHeight = it.size.height },
                    modifier = Modifier.alpha(textAlpha),
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(modifier = Modifier.alpha(textSubtitleAlpha)) {
                ProvideTextStyle(
                    value = MaterialTheme.typography.bodyMedium
                        .copy(color = textColor, fontSize = 12.sp),
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

        // Download view
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
}

@Composable
private fun MangaChapterListItemBackground(state: DismissState) {
    var lastDismissed by remember { mutableStateOf(1) }
    val color = when (state.dismissDirection) {
        DismissDirection.StartToEnd -> {
            lastDismissed = 0
            Color.LightGray
        }
        DismissDirection.EndToStart -> {
            lastDismissed = 1
            MaterialTheme.colorScheme.primary
        }
        else -> {
            when (lastDismissed) {
                0 -> Color.LightGray
                1 -> MaterialTheme.colorScheme.primary
                else -> Color.Red
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color)
            .padding(8.dp),
    ) {
        when (state.dismissDirection) {
            DismissDirection.StartToEnd -> {
                Icon(
                    imageVector = Icons.Default.Bookmark,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.align(Alignment.CenterStart),
                )
            }
            DismissDirection.EndToStart -> {
                Icon(
                    imageVector = Icons.Default.MarkChatRead,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.align(Alignment.CenterEnd),
                )
            }
            else -> {}
        }
    }
}
