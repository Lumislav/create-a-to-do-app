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
fun PhotoFixScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var bgMode by remember { mutableStateOf("transparent") } // transparent, color, gradient, blur
    var cropRatio by remember { mutableStateOf("free") } // free, 1:1, 4:5, 16:9, 9:16
    var rotation by remember { mutableStateOf(0) }
    var isFlippedH by remember { mutableStateOf(false) }

    // Pro adjustments
    var brightness by remember { mutableStateOf(0f) }
    var contrast by remember { mutableStateOf(0f) }
    var saturation by remember { mutableStateOf(0f) }
    var temperature by remember { mutableStateOf(0f) }
    var sharpness by remember { mutableStateOf(0f) }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            viewModel.createProject("Photo Edit", "photofix", uri.toString())
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
                        text = stringResource(R.string.tool_photofix),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MildyTextPrimary
                    )
                }

                IconButton(
                    onClick = { /* Export PNG */ },
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(MildyBrightRed)
                ) {
                    Icon(Icons.Default.Download, contentDescription = "Export", tint = Color.White)
                }
            }
        }

        // Image Canvas Container
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .clip(RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = MildyBgCard)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            when (bgMode) {
                                "color" -> MildyBurgundy
                                "gradient" -> MildyCrimson
                                else -> Color.Transparent
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = null,
                            tint = MildyBrightRed,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (selectedImageUri != null) "Изображение загружено" else "Загрузите фото для обработки",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MildyTextMuted
                        )
                    }
                }
            }
        }

        // Quick Import & AI Buttons
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { imagePicker.launch("image/*") },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MildyBgCard),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = MildyBrightRed)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = stringResource(R.string.action_import_media), color = MildyTextPrimary, fontSize = 12.sp)
                }

                Button(
                    onClick = { /* Run AI Background Removal */ },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MildyBrightRed),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.AutoFixHigh, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = stringResource(R.string.action_remove_bg), color = Color.White, fontSize = 12.sp)
                }
            }
        }

        // Background Replacement Modes
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Замена фона",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MildyTextPrimary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val modes = listOf(
                        Pair("transparent", R.string.bg_transparent),
                        Pair("color", R.string.bg_color),
                        Pair("gradient", R.string.bg_gradient),
                        Pair("blur", R.string.bg_blur)
                    )

                    modes.forEach { (mode, nameRes) ->
                        val isSel = bgMode == mode
                        Button(
                            onClick = { bgMode = mode },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSel) MildyBrightRed else MildyBgCard
                            ),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            Text(
                                text = stringResource(nameRes),
                                fontSize = 11.sp,
                                color = if (isSel) Color.White else MildyTextSecondary
                            )
                        }
                    }
                }
            }
        }

        // Adjustments Sliders
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MildyBgCard),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Цветокоррекция",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MildyTextPrimary
                        )

                        Button(
                            onClick = {
                                brightness = 5f
                                contrast = 12f
                                saturation = 15f
                                sharpness = 20f
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MildyBgSurface),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(stringResource(R.string.action_auto_color), fontSize = 11.sp, color = MildyBrightRed)
                        }
                    }

                    // Brightness
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(stringResource(R.string.brightness), modifier = Modifier.width(90.dp), color = MildyTextSecondary, fontSize = 12.sp)
                        Slider(
                            value = brightness,
                            onValueChange = { brightness = it },
                            valueRange = -50f..50f,
                            modifier = Modifier.weight(1f),
                            colors = SliderDefaults.colors(thumbColor = MildyBrightRed, activeTrackColor = MildyBrightRed)
                        )
                        Text("${brightness.toInt()}", modifier = Modifier.width(36.dp), color = MildyTextMuted, fontSize = 12.sp)
                    }

                    // Contrast
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(stringResource(R.string.contrast), modifier = Modifier.width(90.dp), color = MildyTextSecondary, fontSize = 12.sp)
                        Slider(
                            value = contrast,
                            onValueChange = { contrast = it },
                            valueRange = -50f..50f,
                            modifier = Modifier.weight(1f),
                            colors = SliderDefaults.colors(thumbColor = MildyBrightRed, activeTrackColor = MildyBrightRed)
                        )
                        Text("${contrast.toInt()}", modifier = Modifier.width(36.dp), color = MildyTextMuted, fontSize = 12.sp)
                    }

                    // Saturation
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(stringResource(R.string.saturation), modifier = Modifier.width(90.dp), color = MildyTextSecondary, fontSize = 12.sp)
                        Slider(
                            value = saturation,
                            onValueChange = { saturation = it },
                            valueRange = -50f..50f,
                            modifier = Modifier.weight(1f),
                            colors = SliderDefaults.colors(thumbColor = MildyBrightRed, activeTrackColor = MildyBrightRed)
                        )
                        Text("${saturation.toInt()}", modifier = Modifier.width(36.dp), color = MildyTextMuted, fontSize = 12.sp)
                    }

                    // Sharpness
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(stringResource(R.string.sharpness), modifier = Modifier.width(90.dp), color = MildyTextSecondary, fontSize = 12.sp)
                        Slider(
                            value = sharpness,
                            onValueChange = { sharpness = it },
                            valueRange = 0f..100f,
                            modifier = Modifier.weight(1f),
                            colors = SliderDefaults.colors(thumbColor = MildyBrightRed, activeTrackColor = MildyBrightRed)
                        )
                        Text("${sharpness.toInt()}", modifier = Modifier.width(36.dp), color = MildyTextMuted, fontSize = 12.sp)
                    }
                }
            }
        }

        // Crop & Transform
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Обрезка и трансформация",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MildyTextPrimary
                )

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val ratios = listOf("free", "1:1", "4:5", "16:9", "9:16")
                    items(ratios) { r ->
                        val isSel = cropRatio == r
                        Button(
                            onClick = { cropRatio = r },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSel) MildyBrightRed else MildyBgCard
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(r.uppercase(), fontSize = 12.sp)
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { rotation = (rotation + 90) % 360 },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MildyBgCard),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.RotateRight, contentDescription = null, tint = MildyTextPrimary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Поворот 90°", color = MildyTextPrimary, fontSize = 12.sp)
                    }

                    Button(
                        onClick = { isFlippedH = !isFlippedH },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MildyBgCard),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Flip, contentDescription = null, tint = MildyTextPrimary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Отразить", color = MildyTextPrimary, fontSize = 12.sp)
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
                    onClick = { /* Export PNG */ },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MildyBrightRed),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = stringResource(R.string.action_export_png), fontWeight = FontWeight.Bold, color = Color.White)
                }

                Button(
                    onClick = { /* Export JPG */ },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MildyBgCard),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = stringResource(R.string.action_export_jpg), fontWeight = FontWeight.Bold, color = MildyTextPrimary)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
