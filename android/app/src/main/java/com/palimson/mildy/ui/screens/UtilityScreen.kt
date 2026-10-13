package com.palimson.mildy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
fun UtilityScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    // Utility states
    var pickedHex by remember { mutableStateOf("#FF2A54") }
    var savedPalettes by remember { mutableStateOf(listOf("#FF2A54", "#800020", "#FFE600", "#00F0FF", "#110022")) }
    var watermarkText by remember { mutableStateOf("© Mildy Creator") }
    var watermarkOpacity by remember { mutableStateOf(70f) }
    var resizeWidth by remember { mutableStateOf("1920") }
    var resizeHeight by remember { mutableStateOf("1080") }
    var jpegQuality by remember { mutableStateOf(90f) }
    var compressResolution by remember { mutableStateOf("720p") }

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
                    text = "Дополнительные инструменты",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MildyTextPrimary
                )
            }
        }

        // 1. Color Picker
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MildyBgCard),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Colorize, contentDescription = null, tint = MildyBrightRed)
                        Text(
                            text = stringResource(R.string.tool_color_picker),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MildyTextPrimary
                        )
                    }

                    // Color Preview Swatch
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFFF2A54)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(pickedHex, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("HEX: $pickedHex", color = MildyTextPrimary, fontWeight = FontWeight.SemiBold)
                            Text("RGB: rgb(255, 42, 84)", color = MildyTextMuted, fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                if (pickedHex !in savedPalettes) {
                                    savedPalettes = savedPalettes + pickedHex
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MildyBgSurface),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("В палитру", color = MildyBrightRed, fontSize = 12.sp)
                        }
                    }

                    // Saved Palettes
                    Text("Сохранённые цвета:", color = MildyTextSecondary, fontSize = 12.sp)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(savedPalettes) { colorHex ->
                            val c = try {
                                Color(android.graphics.Color.parseColor(colorHex))
                            } catch (e: Exception) {
                                MildyBrightRed
                            }
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(c)
                                    .clickable { pickedHex = colorHex }
                            )
                        }
                    }
                }
            }
        }

        // 2. Watermark Studio
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MildyBgCard),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Shield, contentDescription = null, tint = MildyBrightRed)
                        Text(
                            text = stringResource(R.string.tool_watermark),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MildyTextPrimary
                        )
                    }

                    OutlinedTextField(
                        value = watermarkText,
                        onValueChange = { watermarkText = it },
                        label = { Text("Текст водяного знака") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = MildyTextPrimary,
                            unfocusedTextColor = MildyTextPrimary,
                            focusedBorderColor = MildyBrightRed,
                            unfocusedBorderColor = MildyBorder
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Прозрачность", modifier = Modifier.width(100.dp), color = MildyTextSecondary, fontSize = 12.sp)
                        Slider(
                            value = watermarkOpacity,
                            onValueChange = { watermarkOpacity = it },
                            valueRange = 10f..100f,
                            modifier = Modifier.weight(1f),
                            colors = SliderDefaults.colors(thumbColor = MildyBrightRed, activeTrackColor = MildyBrightRed)
                        )
                        Text("${watermarkOpacity.toInt()}%", modifier = Modifier.width(38.dp), color = MildyTextMuted, fontSize = 12.sp)
                    }

                    Button(
                        onClick = { /* Export Watermarked */ },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MildyBrightRed),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Применить водяной знак", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // 3. Image Resizer
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MildyBgCard),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Crop, contentDescription = null, tint = MildyBrightRed)
                        Text(
                            text = stringResource(R.string.tool_resizer),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MildyTextPrimary
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = resizeWidth,
                            onValueChange = { resizeWidth = it },
                            label = { Text("Ширина") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = MildyTextPrimary,
                                unfocusedTextColor = MildyTextPrimary,
                                focusedBorderColor = MildyBrightRed,
                                unfocusedBorderColor = MildyBorder
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )

                        OutlinedTextField(
                            value = resizeHeight,
                            onValueChange = { resizeHeight = it },
                            label = { Text("Высота") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = MildyTextPrimary,
                                unfocusedTextColor = MildyTextPrimary,
                                focusedBorderColor = MildyBrightRed,
                                unfocusedBorderColor = MildyBorder
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    Button(
                        onClick = { /* Resize & Download */ },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MildyBgSurface),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Изменить размер & Сохранить", color = MildyBrightRed, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // 4. Video Compressor
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MildyBgCard),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Compress, contentDescription = null, tint = MildyBrightRed)
                        Text(
                            text = stringResource(R.string.tool_compressor),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MildyTextPrimary
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("1080p", "720p", "480p").forEach { res ->
                            val isSel = compressResolution == res
                            Button(
                                onClick = { compressResolution = res },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSel) MildyBrightRed else MildyBgSurface
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(res, fontSize = 12.sp, color = if (isSel) Color.White else MildyTextSecondary)
                            }
                        }
                    }

                    Button(
                        onClick = { /* Compress Video */ },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MildyBrightRed),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Сжать видео ($compressResolution)", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
