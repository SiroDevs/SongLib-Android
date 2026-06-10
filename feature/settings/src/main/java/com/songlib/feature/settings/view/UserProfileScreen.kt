package com.songlib.feature.settings.view

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.songlib.core.ui.components.action.AppTopBar
import com.songlib.feature.settings.UserProfileViewModel

@Composable
fun UserProfileScreen(
    navController: NavHostController,
    viewModel: UserProfileViewModel = hiltViewModel(),
    onSignInRequested: (onResult: (googleId: String, email: String, name: String, photo: String) -> Unit) -> Unit,
) {
    val authState by viewModel.authState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(authState) {
        if (authState is UserProfileViewModel.AuthState.Success) {
            Toast.makeText(context, "Welcome, ${viewModel.userName}! 👋", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title      = "Your Profile",
                tagline    = "Manage your Profile",
                showGoBack = true,
                onNavIconClick = { navController.popBackStack() }
            )
        }
    ) { padding ->
        Column(
            modifier            = Modifier.padding(padding).fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            if (viewModel.isLoggedIn) {
                // ── Logged-in state ─────────────────────────────────────────
                Spacer(Modifier.height(32.dp))

                if (viewModel.userPhotoUrl.isNotEmpty()) {
                    AsyncImage(
                        model             = viewModel.userPhotoUrl,
                        contentDescription = "Profile photo",
                        modifier          = Modifier
                            .size(88.dp)
                            .clip(CircleShape)
                    )
                    Spacer(Modifier.height(16.dp))
                } else {
                    Icon(
                        imageVector       = Icons.Default.AccountCircle,
                        contentDescription = null,
                        modifier          = Modifier.size(88.dp),
                        tint              = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(16.dp))
                }

                Text(viewModel.userName, style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(4.dp))
                Text(
                    text  = viewModel.userEmail,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(32.dp))
                HorizontalDivider()

                ListItem(
                    headlineContent  = { Text("Sign Out") },
                    leadingContent   = { Icon(Icons.Default.Logout, null) },
                    modifier         = androidx.compose.ui.Modifier.then(
                        Modifier.padding(0.dp)
                    ).let {
                        it // clickable added inline below
                    }
                )
                androidx.compose.material3.TextButton(onClick = { viewModel.signOut() }) {
                    Icon(Icons.Default.Logout, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Sign Out")
                }

            } else {
                // ── Guest state ─────────────────────────────────────────────
                Spacer(Modifier.height(48.dp))

                Icon(
                    imageVector       = Icons.Default.AccountCircle,
                    contentDescription = null,
                    modifier          = Modifier.size(88.dp),
                    tint              = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(20.dp))
                Text(
                    text      = "Sign in to sync your likes,\nlistings, drafts, and edits\nacross devices.",
                    textAlign = TextAlign.Center,
                    style     = MaterialTheme.typography.bodyMedium,
                    color     = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier  = Modifier.padding(horizontal = 32.dp)
                )
                Spacer(Modifier.height(28.dp))

                val isLoading = authState is UserProfileViewModel.AuthState.Loading

                Button(
                    onClick = {
                        onSignInRequested { googleId, email, name, photo ->
                            viewModel.loginOrRegister(googleId, email, name, photo)
                        }
                    },
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp))
                    } else {
                        Icon(Icons.Default.Login, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Continue with Google")
                    }
                }

                if (authState is UserProfileViewModel.AuthState.Error) {
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text  = (authState as UserProfileViewModel.AuthState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
