package com.songlib.feature.settings.view.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.songlib.core.common.utils.CrossPromo
import com.songlib.core.common.utils.openAppOrPlayStore
import com.songlib.core.common.utils.openUrl
import com.songlib.core.ui.R as UiR

private data class SiroApp(
    val name: String,
    @DrawableRes val icon: Int,
    val onOpen: (android.content.Context) -> Unit,
)

private val siroApps = listOf(
    SiroApp("BibleLib", UiR.drawable.also_biblelib) {
        openAppOrPlayStore(it, CrossPromo.BIBLELIB_PACKAGE)
    },
    SiroApp("ChurchLib", UiR.drawable.also_churchlib) {
        openUrl(it, CrossPromo.CHURCHLIB_URL)
    },
    SiroApp("SwahiLib", UiR.drawable.also_swahilib) {
        openAppOrPlayStore(it, CrossPromo.SWAHILIB_PACKAGE)
    },
)

@Composable
fun AlsoBySiroDevs() {
    val context = LocalContext.current
    Column {
        SettingsSectionTitle("Also by Siro Devs")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            siroApps.forEach { app ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { app.onOpen(context) }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                ) {
                    Image(
                        painter = painterResource(app.icon),
                        contentDescription = app.name,
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(12.dp)),
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = app.name,
                        style = MaterialTheme.typography.labelMedium,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}
