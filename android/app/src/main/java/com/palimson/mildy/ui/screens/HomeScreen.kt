package com.palimson.mildy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.palimson.mildy.R
import com.palimson.mildy.navigation.Screen
import com.palimson.mildy.ui.components.QuickToolCard
import com.palimson.mildy.ui.theme.*
import com.palimson.mildy.ui.viewmodel.MainViewModel

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onNavigate: (String) -> Unit
) {
    val recentProjects by viewModel.recentProjects.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MildyBgPrimary)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            // Hero Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(containerColor = MildyBgCard),
                shape = RoundedCornerShape(24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(MildyBgCardHover, MildyBurgundy.copy(alpha = 0.35f))
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = stringResource(R.string.greeting),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MildyTextPrimary
                        )
                        Text(
                            text = stringResource(R.string.greeting_sub),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MildyTextSecondary
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Button(
                            onClick = { onNavigate(Screen.Tools.route) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MildyBrightRed
                            ),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.new_project),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        // Quick Tools Grid
        item {
            Text(
                text = stringResource(R.string.quick_actions),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MildyTextPrimary
            )
            Spacer(modifier = Modifier.height(10.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickToolCard(
                        title = stringResource(R.string.tool_subtitles),
                        subtitle = "Word-level sync",
                        icon = Icons.Default.ClosedCaption,
                        gradient = listOf(MildyBrightRed, MildyCrimson),
                        onClick = { onNavigate(Screen.Subtitles.route) },
                        modifier = Modifier.weight(1f)
                    )
                    QuickToolCard(
                        title = stringResource(R.string.tool_photofix),
                        subtitle = "AI Background",
                        icon = Icons.Default.AutoFixHigh,
                        gradient = listOf(MildyCrimson, MildyBurgundy),
                        onClick = { onNavigate(Screen.PhotoFix.route) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickToolCard(
                        title = stringResource(R.string.tool_video_tools),
                        subtitle = "9:16 Shorts & Trim",
                        icon = Icons.Default.Videocam,
                        gradient = listOf(MildyRose, MildyBrightRed),
                        onClick = { onNavigate(Screen.VideoTools.route) },
                        modifier = Modifier.weight(1f)
                    )
                    QuickToolCard(
                        title = stringResource(R.string.tool_thumbnail),
                        subtitle = "Cover Studio",
                        icon = Icons.Default.Image,
                        gradient = listOf(MildyBurgundy, MildyBgCard),
                        onClick = { onNavigate(Screen.Thumbnail.route) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Recent Projects Section
        item {
            Text(
                text = stringResource(R.string.recent_projects),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MildyTextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (recentProjects.isEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MildyBgCard),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Начните работу, создав первый проект!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MildyTextMuted
                        )
                    }
                }
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(recentProjects) { project ->
                        Card(
                            modifier = Modifier
                                .width(170.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .clickable {
                                    val dest = when (project.toolType) {
                                        "subtitles" -> Screen.Subtitles.route
                                        "photofix" -> Screen.PhotoFix.route
                                        "videotools" -> Screen.VideoTools.route
                                        "thumbnail" -> Screen.Thumbnail.route
                                        else -> Screen.Tools.route
                                    }
                                    onNavigate(dest)
                                },
                            colors = CardDefaults.cardColors(containerColor = MildyBgCard)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(90.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(MildyBgSurface),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Folder,
                                        contentDescription = null,
                                        tint = MildyRose,
                                        modifier = Modifier.size(36.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = project.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MildyTextPrimary,
                                    maxLines = 1
                                )
                                Text(
                                    text = project.toolType.replaceFirstChar { it.uppercase() },
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MildyTextMuted
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
