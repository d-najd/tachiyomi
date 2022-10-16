package eu.kanade.presentation.library.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import eu.kanade.tachiyomi.R

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun BoxScope.LibraryGridItemLastReadButton(
    mangaId: Long,
    onClickLastRead: (Long) -> Unit,
) {
    FloatingActionButton(
        onClick = { onClickLastRead(mangaId) },
        // backgroundColor = Color.Red,
        modifier = Modifier
            // .navigationBarsPadding()
            .padding(6.dp)
            .size(32.dp)
            .align(Alignment.BottomEnd),
        content = {
            Icon(
                painter = painterResource(R.drawable.ic_continue_reading_24dp),
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.Center),
                // tint = Color.White
            )
        },
    )

    /*
    ExtendedFloatingActionButton(
        onClick = { onClickLastRead(mangaId) },
        icon = { Icon(imageVector = Icons.Outlined.Add, contentDescription = "") },
        modifier = Modifier
            .navigationBarsPadding()
            //.padding(8.dp)
            .size(24.dp)
            .align(Alignment.BottomEnd),

    )

     */
    /*
    Image(
        painterResource(R.drawable.ic_continue_reading_24dp),
        contentDescription = "",
        modifier = Modifier
            .padding(8.dp)
            .size(24.dp)
            .align(Alignment.BottomEnd)
            .clickable { onClickLastRead(mangaId) },
    )
     */
}
