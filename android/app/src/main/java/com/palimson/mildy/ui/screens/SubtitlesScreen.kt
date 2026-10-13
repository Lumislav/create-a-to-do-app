package com.palimson.mildy.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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

data class SubtitleWord(val word: String, val start: Double, val end: Double)
data class SubtitleLine(val id: Long, val start: Double, val end: Double, val text: String, val words: List<SubtitleWord>)

@Composable
fun SubtitlesScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    var selectedVideoUri by remember { mutableStateOf<Uri?>(null) }
    var selectedLang by remember { mutableStateOf("ru") }
    var selectedStyle by remember { mutableStateOf("viral") } // viral, beast, clean, neon, cyber
    var subtitlePosition by remember { mutableStateOf("bottom") } // bottom, center, top
    var highlightColor by remember { mutableStateOf(Color(0xFFFFE600)) }
    var isTranscribing by remember { mutableStateOf(false) }

    var subtitlesList by remember {
        mutableStateOf(
            listOf(
                SubtitleLine(
                    1, 0.4, 2.7, "Всем привет вы смотрите студию Mildy",
                    listOf(
                        SubtitleWord("Всем", 0.4, 0.8),
                        SubtitleWord("привет", 0.8, 1.2),
                        SubtitleWord("вы", 1.2, 1.4),
                        SubtitleWord("смотрите", 1.4, 1.8),
                        SubtitleWord("студию", 1.8, 2.2),
                        SubtitleWord("Mildy", 2.2, 2.7)
                    )
                ),
                SubtitleLine(
                    2, 3.0, 5.2, "Автоматические субтитры и пословная подсветка",
                    listOf(
                        SubtitleWord("Автоматические", 3.0, 3.6),
                        SubtitleWord("субтитры", 3.6, 4.2),
                        SubtitleWord("и", 4.2, 4.4),
                        SubtitleWord("пословная", 4.4, 4.8),
                        SubtitleWord("подсветка", 4.8, 5.2)
                    )
                ),
                SubtitleLine(
                    3, 5.5, 8.0, "Создавайте вирусные видео прямо сейчас",
                    listOf(
                        SubtitleWord("Создавайте", 5.5, 6.1),
                        SubtitleWord("вирусные", 6.1, 6.7),
                        SubtitleWord("видео", 6.7, 7.2),
                        SubtitleWord("прямо", 7.2, 7.6),
                        SubtitleWord("сейчас", 7.6, 8.0)
                    )
                )
            )
        )
    }

    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedVideoUri = uri
            viewModel.createProject("Subtitles Video", "subtitles", uri.toString())
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
                        text = stringResource(R.string.tool_subtitles),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MildyTextPrimary
                    )
                }

                IconButton(
                    onClick = { /* Export burned video */ },
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(MildyBrightRed)
                ) {
                    Icon(Icons.Default.Download, contentDescription = "Export", tint = Color.White)
                }
            }
        }

        // Video Player Container with Live Subtitles Overlay
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .clip(RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = MildyBgSecondary)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MildyBgCard),
                    contentAlignment = when (subtitlePosition) {
                        "top" -> Alignment.TopCenter
                        "center" -> Alignment.Center
                        else -> Alignment.BottomCenter
                    }
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.align(Alignment.Center)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayCircle,
                            contentDescription = null,
                            tint = MildyBrightRed,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (selectedVideoUri != null) "Видео загружено" else "Выберите видео или используйте демо",
                            style = MaterialTheme.typography.bodySmall,
                            color = MildyTextMuted
                        )
                    }

                    // Live Subtitle Overlay Preview
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 20.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (selectedStyle == "clean") Color(0xAA000000) else Color.Transparent)
                            .padding(8.dp)
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "СОЗДАВАЙТЕ ",
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                color = highlightColor
                            )
                            Text(
                                text = "ВИРУСНЫЕ ВИДЕО",
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        // Media Action Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { videoPickerLauncher.launch("video/*") },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MildyBgCard),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.VideoLibrary, contentDescription = null, tint = MildyBrightRed)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = stringResource(R.string.action_import_media), color = MildyTextPrimary, fontSize = 12.sp)
                }

                Button(
                    onClick = {
                        isTranscribing = true
                        // Real Whisper Speech Transcription Trigger
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MildyBrightRed),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Mic, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = stringResource(R.string.action_transcribe), color = Color.White, fontSize = 12.sp)
                }
            }
        }

        // Subtitle Style Presets
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Стиль субтитров",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MildyTextPrimary
                )

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val styles = listOf(
                        Pair("viral", "TikTok Viral"),
                        Pair("beast", "MrBeast Bold"),
                        Pair("clean", "Minimal Clean"),
                        Pair("neon", "Wine Neon"),
                        Pair("cyber", "Cyber Gold")
                    )

                    items(styles) { (styleId, name) ->
                        val isSel = selectedStyle == styleId
                        FilterChip(
                            selected = isSel,
                            onClick = {
                                selectedStyle = styleId
                                highlightColor = when (styleId) {
                                    "viral" -> Color(0xFFFFE600)
                                    "beast" -> Color(0xFF00FF66)
                                    "neon" -> MildyBrightRed
                                    "cyber" -> Color(0xFF00FFA3)
                                    else -> Color.White
                                }
                            },
                            label = { Text(name) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MildyBrightRed,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
        }

        // Position & Language Selectors
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Позиция",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MildyTextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf("bottom", "center", "top").forEach { pos ->
                            Button(
                                onClick = { subtitlePosition = pos },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (subtitlePosition == pos) MildyBrightRed else MildyBgCard
                                ),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(pos.replaceFirstChar { it.uppercase() }, fontSize = 11.sp)
                            }
                        }
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Язык модели",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MildyTextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf(
                            Pair("ru", "RU"),
                            Pair("kk", "KZ"),
                            Pair("en", "EN")
                        ).forEach { (code, label) ->
                            Button(
                                onClick = { selectedLang = code },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (selectedLang == code) MildyBrightRed else MildyBgCard
                                ),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(label, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        // Subtitle Timeline Lines Editor
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Таймлайн субтитров",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MildyTextPrimary
                )

                Button(
                    onClick = {
                        val last = subtitlesList.lastOrNull()
                        val newStart = (last?.end ?: 0.0) + 0.3
                        val newEnd = newStart + 2.0
                        subtitlesList = subtitlesList + SubtitleLine(
                            id = System.currentTimeMillis(),
                            start = newStart,
                            end = newEnd,
                            text = "Новая строка субтитров",
                            words = listOf(
                                SubtitleWord("Новая", newStart, newStart + 0.6),
                                SubtitleWord("строка", newStart + 0.6, newStart + 1.2),
                                SubtitleWord("субтитров", newStart + 1.2, newEnd)
                            )
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MildyBgCard),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(stringResource(R.string.action_add_line), fontSize = 11.sp, color = MildyBrightRed)
                }
            }
        }

        itemsIndexed(subtitlesList) { index, line ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MildyBgCard),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(MildyBgSurface)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "%.1fs - %.1fs".format(line.start, line.end),
                            style = MaterialTheme.typography.labelSmall,
                            color = MildyRose,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    OutlinedTextField(
                        value = line.text,
                        onValueChange = { newTxt ->
                            val updated = subtitlesList.toMutableList()
                            updated[index] = line.copy(text = newTxt)
                            subtitlesList = updated
                        },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = MildyTextPrimary,
                            unfocusedTextColor = MildyTextPrimary,
                            focusedBorderColor = MildyBrightRed,
                            unfocusedBorderColor = MildyBorder
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )

                    IconButton(
                        onClick = {
                            val updated = subtitlesList.toMutableList()
                            updated.removeAt(index)
                            subtitlesList = updated
                        }
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MildyRed)
                    }
                }
            }
        }

        // Export Actions
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { /* Export SRT */ },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MildyBgCard),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Description, contentDescription = null, tint = MildyTextPrimary)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = stringResource(R.string.action_export_srt), color = MildyTextPrimary, fontSize = 12.sp)
                }

                Button(
                    onClick = { /* Export VTT */ },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MildyBgCard),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Code, contentDescription = null, tint = MildyTextPrimary)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = stringResource(R.string.action_export_vtt), color = MildyTextPrimary, fontSize = 12.sp)
                }
            }
        }

        item {
            Button(
                onClick = { /* Export burned video */ },
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
