package com.example.clickretina_assignment

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.clickretina_assignment.api.Social
import com.example.clickretina_assignment.ui.theme.MyProfileAppTheme

@Composable
fun UserProfileScreen(viewModel: ProfileViewModel, onLogout: () -> Unit) {
    val profileResult = viewModel.profileResult.observeAsState()
    val context = LocalContext.current
    var showLogoutDialog by remember { mutableStateOf(false) }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout") },
            text = { Text("Are you sure you want to log out?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    }
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("No")
                }
            }
        )
    }


    LaunchedEffect(key1 = true) {
        Toast.makeText(context, "Welcome to the DashBoard Screen", Toast.LENGTH_SHORT).show()
        viewModel.fetchUserProfile(context)
    }

    MyProfileAppTheme {
        when (val result = profileResult.value) {
            is NetworkResponse.Error -> {
                if (!NetworkUtils.isNetworkAvailable(LocalContext.current)) {
                    // If no internet, show the specific NoInternetScreen.
                    NoInternetScreen(onRetry = { viewModel.fetchUserProfile(context) })
                } else {
                    // Otherwise, show the generic error screen for server issues.
                    ErrorScreen(
                        errorMessage = result.message,
                        onRetry = { viewModel.fetchUserProfile(context) }
                    )
                }
            }
            is NetworkResponse.Loading -> LoadingScreen()
            is NetworkResponse.Success -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        // Use the theme's background color
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    item {
                        ProfileHeader(result.data.user.avatar, userName = result.data.user.username) { showLogoutDialog = true }
                    }
                    item {
                        UserInfoSection(
                            result.data.user.name,
                            userCountry = result.data.user.location.country,
                            userCity = result.data.user.location.city
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                        StatsSection(
                            followers = result.data.user.statistics.followers,
                            following = result.data.user.statistics.following
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                        SocialLinksSection(social = result.data.user.social)
                    }
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                        TabsSection(
                            shots = result.data.user.statistics.activity.shots,
                            collections = result.data.user.statistics.activity.collections
                        )
                    }
                }
            }
            null -> {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize(),
                ) {
                    Text("SOME UNKNOWN ERROR OCCURED")
                }
            }
        }
    }
}

@Composable
fun ErrorScreen(errorMessage: String, onRetry: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = "Error Icon",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Something went wrong",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onRetry) {
                Text("Try Again")
            }
        }
    }
}

@Composable
fun LoadingScreen() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 4.dp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Loading Profile...",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
fun ProfileHeader(profileImageUrl: String, userName: String, onSettingsClick: () -> Unit) {
    val headerHeight = 220.dp
    val profilePicSize = 100.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(headerHeight + profilePicSize / 2),
        contentAlignment = Alignment.TopCenter
    ) {
        // The gradient is a brand element, so it can remain the same
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(headerHeight)
                .clip(ArcShape())
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF8E2DE2), Color(0xFF4A00E0))
                    )
                )
        )

        // Top bar content: username and settings icon
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 60.dp, start = 16.dp, end = 16.dp)
        ) {
            Text(
                text = userName,
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )
            IconButton(
                onClick = { onSettingsClick() },
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = Color.White
                )
            }
        }

        // Profile picture
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current).data(profileImageUrl).crossfade(true).build(),
            contentDescription = "Profile Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-30).dp)
                .size(profilePicSize)
                .clip(CircleShape)
                // This border uses the screen background color to create a "cutout" effect
                .border(4.dp, MaterialTheme.colorScheme.background, CircleShape)
        )
    }
}

@Composable
fun UserInfoSection(userName: String, userCountry: String, userCity: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = userName,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "$userCity,  $userCountry",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun StatsSection(followers: Int, following: Int) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant, // Use theme surface color
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatItem(count = followers.toString(), label = "Followers")
            Divider(
                modifier = Modifier
                    .width(1.dp)
                    .height(30.dp),
                color = MaterialTheme.colorScheme.outlineVariant // Use theme divider color
            )
            StatItem(count = following.toString(), label = "Following")
        }
    }
}

@Composable
fun StatItem(count: String, label: String) {
    Row {
        Text(
            text = count,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant // Use theme text color
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurfaceVariant, // Use theme text color
            fontSize = 15.sp
        )
    }
}


@Composable
fun SocialLinksSection(social: Social) {
    val context = LocalContext.current
    fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        var isFirstIcon = true
        if (!social.website.isNullOrBlank()) {
            isFirstIcon = false
            IconButton(onClick = { openUrl(social.website) }) {
                Icon(
                    painter = painterResource(id = R.drawable.internet),
                    contentDescription = "Website",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(25.dp)
                )
            }
        }
        social.profiles.forEach { profile ->
            val iconResId = when (profile.platform.lowercase()) {
                "instagram" -> R.drawable.instagram
                "facebook" -> R.drawable.facebook
                else -> null
            }

            if (iconResId != null) {
                if (!isFirstIcon) {
                    Spacer(modifier = Modifier.width(15.dp))
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(color = Color(0xFF2C4EC0)) // Brand color can stay
                    )
                    Spacer(modifier = Modifier.width(15.dp))
                }
                IconButton(onClick = { openUrl(profile.url) }) {
                    Icon(
                        painter = painterResource(id = iconResId),
                        contentDescription = profile.platform,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(25.dp)
                    )
                }
                isFirstIcon = false
            }
        }
    }
}


@Composable
fun TabsSection(shots: Int, collections: Int) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Shots", "Collections")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.primary,
            indicator = {},
            divider = {}
        ) {
            tabs.forEachIndexed { index, title ->
                val isSelected = selectedTabIndex == index
                Tab(
                    selected = isSelected,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            text = if (index == 0) "$shots $title" else "$collections $title",
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    modifier = Modifier
                        .padding(horizontal = 4.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
                        )
                )
            }
        }
        when (selectedTabIndex) {
            0 -> ShotsContent()
            1 -> CollectionsContent()
        }
    }
}

@Composable
fun ShotsContent() {
    Spacer(modifier = Modifier.height(50.dp))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .size(170.dp)
            .padding(top = 25.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.psillustration),
            contentDescription = "No shots available",
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun CollectionsContent() {
    Spacer(modifier = Modifier.height(50.dp))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .size(170.dp)
            .padding(top = 25.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.psillustration),
            contentDescription = "No collections available",
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun NoInternetScreen(onRetry: () -> Unit) {
    val context = LocalContext.current

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "No Internet",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No Internet Connection",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Please check your connection and try again.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = {
                // Intent to open Wi-Fi settings
                context.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
            }) {
                Text("Open Settings")
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(onClick = onRetry) {
                Text("Retry")
            }
        }
    }
}

class ArcShape : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val path = Path().apply {
            moveTo(0f, 0f)
            lineTo(size.width, 0f)
            lineTo(size.width, size.height * 0.75f)
            quadraticBezierTo(
                x1 = size.width / 2,
                y1 = size.height,
                x2 = 0f,
                y2 = size.height * 0.75f
            )
            close()
        }
        return Outline.Generic(path)
    }
}

@Preview
@Composable
private fun ItsPreview() {
    UserProfileScreen(viewModel = ProfileViewModel(), onLogout = {})
}
