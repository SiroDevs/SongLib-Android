package com.songlib.feature.casting.view

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.songlib.core.casting.data.ServerStatus
import com.songlib.core.ui.components.action.AppTopBar
import com.songlib.feature.casting.viewmodel.CastingViewModel
import com.songlib.feature.casting.view.components.ConnectCard
import com.songlib.feature.casting.view.components.ExplainerCard
import com.songlib.feature.casting.view.components.StatusCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CastingScreen(
    navController: NavHostController,
    viewModel: CastingViewModel,
) {
    val serverStatus by viewModel.serverStatus.collectAsState()
    val slideState by viewModel.slideState.collectAsState()
    val connectedClients by viewModel.connectedClients.collectAsState()
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* notification just won't show if denied — castinging still works */ }

    fun requestStart() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        viewModel.startCasting()
    }

    val isCasting = serverStatus is ServerStatus.Running || serverStatus is ServerStatus.Starting

    Scaffold(
        topBar = {
            AppTopBar(
                title = "SongLib Casting",
                tagline = "Disclaimer: This is a WIP Feature",
                showGoBack = true,
                onNavIconClick = { navController.popBackStack() },
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (!isCasting) {
                ExplainerCard()
            }

            StatusCard(
                serverStatus = serverStatus,
                connectedClients = connectedClients,
                slideState = slideState,
                onStart = ::requestStart,
                onStop = viewModel::stopCasting,
            )

//            if (isCasting) {
//                ConnectCard(
//                    urls = (serverStatus as ServerStatus.Running).urls,
//                    onCopy = { url ->
//                        clipboard.setText(AnnotatedString(url))
//                        Toast.makeText(context, "Link copied", Toast.LENGTH_SHORT).show()
//                    },
//                    onOpenHotspotSettings = { openTetherSettings(context) },
//                )
//            }
        }
    }
}

private fun openTetherSettings(context: Context) {
    val candidates = listOf(
        "android.settings.TETHER_SETTINGS",
        android.provider.Settings.ACTION_WIRELESS_SETTINGS,
    )
    for (action in candidates) {
        try {
            context.startActivity(Intent(action).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            return
        } catch (_: Exception) {
            // try the next one
        }
    }
}
