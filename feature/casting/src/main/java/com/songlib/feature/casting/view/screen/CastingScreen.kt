package com.songlib.feature.casting.view.screen

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.songlib.core.common.entity.HotspotStatus
import com.songlib.core.common.entity.ServerStatus
import com.songlib.core.ui.components.action.AppTopBar
import com.songlib.feature.casting.view.components.ConnectCard
import com.songlib.feature.casting.view.components.ExplainerCard
import com.songlib.feature.casting.view.components.StatusCard
import com.songlib.feature.casting.viewmodel.CastingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CastingScreen(
    navController: NavHostController,
    viewModel: CastingViewModel,
) {
    val serverStatus by viewModel.serverStatus.collectAsState()
    val slideState by viewModel.slideState.collectAsState()
    val connectedClients by viewModel.connectedClients.collectAsState()
    val hotspotStatus by viewModel.hotspotStatus.collectAsState()
    val wifiConnected by viewModel.wifiConnected.collectAsState()
    val hasExternalHotspot by viewModel.hasExternalHotspot.collectAsState()
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current

    var showNetworkPrompt by remember { mutableStateOf(false) }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* notification just won't show if denied — casting still works */ }

    val hotspotPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.NEARBY_WIFI_DEVICES
    } else {
        Manifest.permission.ACCESS_FINE_LOCATION
    }

    val hotspotPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> if (granted) viewModel.startHotspot() }

    fun requestStartHotspot() {
        val granted = ContextCompat.checkSelfPermission(context, hotspotPermission) ==
            PackageManager.PERMISSION_GRANTED
        if (granted) viewModel.startHotspot() else hotspotPermissionLauncher.launch(hotspotPermission)
    }

    fun requestStartCasting() {
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

    fun requestStart() {
        val hotspotRunning = hotspotStatus is HotspotStatus.Running
        if (!wifiConnected && !hotspotRunning && !hasExternalHotspot) {
            showNetworkPrompt = true
            return
        }
        requestStartCasting()
    }

    if (showNetworkPrompt) {
        AlertDialog(
            onDismissRequest = { showNetworkPrompt = false },
            title = { Text("Before you Start Casting ...") },
            text = {
                Text(
                    "The devices you are casting to - PC or phones need to reach this phone over Wi-Fi. " +
                            "\n\nJoin a Wi-Fi network, or let SongLib create its own hotspot just for casting. " +
                            "\n\nNo mobile data will be shared in the casting."
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showNetworkPrompt = false
                    requestStartHotspot()
                }) { Text("Start a Hotspot") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showNetworkPrompt = false
                    openWifiSettings(context)
                }) { Text("Join a Wi-Fi") }
            }
        )
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

            if (isCasting) {
                ConnectCard(
                    castingUrl = (serverStatus as? ServerStatus.Running)?.url,
                    hotspotStatus = hotspotStatus,
                    wifiConnected = wifiConnected,
                    hasExternalHotspot = hasExternalHotspot,
                    onStartHotspot = ::requestStartHotspot,
                    onStopHotspot = viewModel::stopHotspot,
                    onCopyUrl = { url ->
                        clipboard.setText(AnnotatedString(url))
                        Toast.makeText(context, "Link copied", Toast.LENGTH_SHORT).show()
                    },
                )
            }
        }
    }
}

private fun openWifiSettings(context: Context) {
    try {
        context.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    } catch (_: Exception) {
        // nothing more we can do
    }
}
