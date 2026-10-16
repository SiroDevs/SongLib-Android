package com.songlib.feature.home.view.components.promo

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.songlib.core.common.utils.CrossPromo
import com.songlib.core.common.utils.openUrl
import com.songlib.core.data.repos.PrefsRepo
import com.songlib.core.ui.R as UiR
import kotlinx.coroutines.delay

@Composable
fun ChurchLibPromo(prefsRepo: PrefsRepo, enabled: Boolean) {
    val context = LocalContext.current
    var show by remember { mutableStateOf(false) }

    LaunchedEffect(enabled, show) {
        if (!enabled || show) return@LaunchedEffect
        val wait = prefsRepo.churchLibPromoWaitMs() ?: return@LaunchedEffect
        delay(wait)
        show = true
    }

    if (!show || !enabled) return

    AlertDialog(
        onDismissRequest = {},
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false),
        icon = {
            Image(
                painter = painterResource(UiR.drawable.also_churchlib),
                contentDescription = null,
                modifier = Modifier.size(48.dp),
            )
        },
        title = { Text("Introducing ChurchLib") },
        text = {
            Text(
                "You can now enjoy ChurchLib on a desktop computer either in the church " +
                        "laptop or your PC. ChurchLib combines the experience of both SongLib " +
                        "and BibleLib into one desktop software with ease."
            )
        },
        confirmButton = {
            TextButton(onClick = {
                prefsRepo.markChurchLibPromoSeen()
                show = false
                openUrl(context, CrossPromo.CHURCHLIB_URL)
            }) { Text("Learn More") }
        },
        dismissButton = {
            TextButton(onClick = {
                prefsRepo.remindChurchLibPromoLater()
                show = false
            }) { Text("Remind me later") }
        },
    )
}
