package eu.kanade.presentation.library.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import eu.kanade.tachiyomi.R

@Composable
fun BoxScope.LibraryGridItemLastReadButton(
    mangaId: Long,
    onClickLastRead: (Long) -> Unit,
) {
    OutlinedButton(
        onClick = { onClickLastRead(mangaId) },
        modifier = Modifier
            .size(34.dp)
            .offset(x = (-5).dp, y = (-5).dp) // padding makes the icon smaller
            .align(Alignment.BottomEnd),
        shape = CircleShape,
        border = BorderStroke(1.dp, Color.White),
        contentPadding = PaddingValues(0.dp), // avoid the little icon
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Color.White,
            containerColor = Color.Black.copy(.4f),
        ),
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_continue_reading_24dp),
            contentDescription = null,
            modifier = Modifier
                .offset(y = (-.75).dp) // make the icon more "centered"
                .size(24.dp),
        )
    }
}
