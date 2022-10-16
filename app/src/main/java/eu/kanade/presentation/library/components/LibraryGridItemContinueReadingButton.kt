package eu.kanade.presentation.library.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import eu.kanade.tachiyomi.R

@Composable
fun BoxScope.LibraryGridItemLastReadButton(
    mangaId: Long,
    onClickLastRead: (Long) -> Unit,
) {
    Image(
        painterResource(R.drawable.ic_continue_reading_24dp),
        contentDescription = "",
        modifier = Modifier
            .padding(8.dp)
            .size(24.dp)
            .align(Alignment.BottomEnd)
            .clickable { onClickLastRead(mangaId) },
    )
}
