package com.palimson.mildy.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.palimson.mildy.R
import com.palimson.mildy.ui.theme.*
import com.palimson.mildy.ui.viewmodel.MainViewModel

@Composable
fun VideoToolsScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    var selectedVideoUri by remember { mutableStateOf<Uri?>(null) }
    var aspectRatio by remember { mutableStateOf("9:16") } // 9:16, 16:9, 1:1, 4:5, original
    var trimStart by remember { mutableStateOf(0f) }
    var trimEnd by remember { mutableStateOf(10f) }
    var playbackSpeed by remember { mutableStateOf(1.0f) }
    var volume by remember { mutableStateOf(1.0f) }
    var isMuted by remember { mutableStateOf(false) }
    var overlayText by remember { mutableStateOf("") }

    val videoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedVideoUri = uri
            viewModel.createProject("Short Video Edit", "videotools", uri.toString())
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MildyBgPrimary)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Topbar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(MildyBgCard)
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = MildyTextPrimary)
                    }
                    Text(
                        text = stringResource(R.string.tool_video_tools),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MildyTextPrimary
                    )
                }

                IconButton(
                    onClick = { /* Export MP4 */ },
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(MildyBrightRed)
                ) {
                    Icon(Icons.Default.Download, contentDescription = "Export", tint = Color.White)
                }
            }
        }

        // Video Player Preview
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(
                        when (aspectRatio) {
                            "9:16" -> 320.dp
                            "1:1" -> 240.dp
                            else -> 200.dp
                        }
                    )
                    .clip(RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = MildyBgCard)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayCircle,
                            contentDescription = null,
                            tint = MildyBrightRed,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (selectedVideoUri != null) "Видео загружено ($aspectRatio)" else "Выберите видеофайл для монтажа",
                            style = MaterialTheme.typography.bodySmall,
                            color = MildyTextMuted
                        )
                    }

                    if (overlayText.isNotEmpty()) {
                        Text(
                            text = overlayText,
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp,
                            color = Color.White,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 24.dp)
                        )
                    }
                }
            }
        }

        // Import Button
        item {
            Button(
                onClick = { videoPicker.launch("video/*") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MildyBgCard),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.VideoLibrary, contentDescription = null, tint = MildyBrightRed)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = stringResource(R.string.action_import_media), color = MildyTextPrimary)
            }
        }

        // Aspect Ratio Selector
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = stringResource(R.string.aspect_ratio),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MildyTextPrimary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val ratios = listOf(
                        Pair("9:16", "9:16 Shorts"),
                        Pair("16:9", "16:9 YT"),
                        Pair("1:1", "1:1"),
                        Pair("4:5", "4:5 Reels")
                    )

                    ratios.forEach { (ratio, label) ->
                        val isSel = aspectRatio == ratio
                        Button(
                            onClick = { aspectRatio = ratio },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSel) MildyBrightRed else MildyBgCard
                            ),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            Text(label, fontSize = 11.sp, color = if (isSel) Color.White else MildyTextSecondary)
                        }
                    }
                }
            }
        }

        // Timeline Trimming
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MildyBgCard),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = stringResource(R.string.trim_video),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MildyTextPrimary
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Начало: %.1fs".format(trimStart), color = MildyTextSecondary, fontSize = 12.sp)
                        Text("Конец: %.1fs".format(trimEnd), color = MildyTextSecondary, fontSize = 12.sp)
                    }

                    RangeSlider(
                        value = trimStart..trimEnd,
                        onValueChange = { range ->
                            trimStart = range.start
                            trimEnd = range.endInclusive
                        },
                        valueRange = 0f..60f,
                        colors = SliderDefaults.colors(
                            thumbColor = MildyBrightRed,
                            activeTrackColor = MildyBrightRed
                        )
                    )
                }
            }
        }

        // Playback Speed
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = stringResource(R.string.speed),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MildyTextPrimary
                )

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val speeds = listOf(0.5f, 1.0f, 1.25f, 1.5f, 2.0f)
                    items(speeds) { spd ->
                        val isSel = playbackSpeed == spd
                        Button(
                            onClick = { playbackSpeed = spd },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSel) MildyBrightRed else MildyBgCard
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text("${spd}x", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Text Overlay Input
        item {
            OutlinedTextField(
                value = overlayText,
                onValueChange = { overlayText = it },
                label = { Text("Текст поверх видео") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MildyTextPrimary,
                    unfocusedTextColor = MildyTextPrimary,
                    focusedBorderColor = MildyBrightRed,
                    unfocusedBorderColor = MildyBorder
                ),
                shape = RoundedCornerShape(12.dp)
            )
        }

        // Frame Grabber & Audio Extraction Buttons
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { /* Save Frame */ },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MildyBgCard),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null, tint = MildyBrightRed)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Сохранить кадр", color = MildyTextPrimary, fontSize = 12.sp)
                }

                Button(
                    onClick = { /* Extract Audio */ },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MildyBgCard),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.MusicNote, contentDescription = null, tint = MildyBrightRed)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Извлечь аудио", color = MildyTextPrimary, fontSize = 12.sp)
                }
            }
        }

        // Final Export
        item {
            Button(
                onClick = { /* Export Video */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MildyBrightRed),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.Movie, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.action_export_video),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
