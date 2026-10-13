package com.palimson.mildy.ui.screens

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.palimson.mildy.R
import com.palimson.mildy.ui.theme.*
import com.palimson.mildy.ui.viewmodel.MainViewModel

data class CanvasLayer(
    val id: Long,
    val type: String, // "text", "badge", "image"
    val content: String,
    val color: Color = Color.White,
    val fontSize: Int = 28
)

@Composable
fun ThumbnailScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    var canvasPreset by remember { mutableStateOf("16:9") } // 16:9, 9:16, 1:1
    var layers by remember {
        mutableStateOf(
            listOf(
                CanvasLayer(1, "text", "MILDY STUDIO", Color(0xFFFF2A54), 32),
                CanvasLayer(2, "badge", "🔥 ТОП КОНТЕНТ", Color.Yellow, 18),
                CanvasLayer(3, "text", "Как делать субтитры за 5 минут", Color.White, 24)
            )
        )
    }
    var selectedLayerIndex by remember { mutableStateOf(0) }

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
                        text = stringResource(R.string.tool_thumbnail),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MildyTextPrimary
                    )
                }

                IconButton(
                    onClick = { /* Export Cover PNG */ },
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(MildyBrightRed)
                ) {
                    Icon(Icons.Default.Download, contentDescription = "Export", tint = Color.White)
                }
            }
        }

        // Canvas Preview
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (canvasPreset == "16:9") 200.dp else 260.dp)
                    .clip(RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = MildyBgCard)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                listOf(MildyBgSecondary, MildyBurgundy, MildyBgPrimary)
                            )
                        )
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        layers.forEachIndexed { idx, layer ->
                            val isSelected = idx == selectedLayerIndex
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) Color(0x33FF2A54) else Color.Transparent)
                                    .clickable { selectedLayerIndex = idx }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = layer.content,
                                    color = layer.color,
                                    fontSize = layer.fontSize.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }
                }
            }
        }

        // Canvas Format Selector
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Формат обложки",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MildyTextPrimary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val formats = listOf(
                        Pair("16:9", "YouTube (16:9)"),
                        Pair("9:16", "Shorts (9:16)"),
                        Pair("1:1", "Square (1:1)")
                    )

                    formats.forEach { (fmt, label) ->
                        val isSel = canvasPreset == fmt
                        Button(
                            onClick = { canvasPreset = fmt },
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

        // Add Layer Buttons
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        val newLayer = CanvasLayer(System.currentTimeMillis(), "text", "Новый текст", Color.White, 24)
                        layers = layers + newLayer
                        selectedLayerIndex = layers.size - 1
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MildyBgCard),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("+ Текст", fontSize = 12.sp, color = MildyTextPrimary)
                }

                Button(
                    onClick = {
                        val newLayer = CanvasLayer(System.currentTimeMillis(), "badge", "⭐ БЕЙДЖ", Color.Yellow, 18)
                        layers = layers + newLayer
                        selectedLayerIndex = layers.size - 1
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MildyBgCard),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("+ Бейдж", fontSize = 12.sp, color = MildyTextPrimary)
                }

                Button(
                    onClick = { /* Remove BG from selected layer */ },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MildyBgCard),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Удалить фон", fontSize = 11.sp, color = MildyBrightRed)
                }
            }
        }

        // Selected Layer Editor
        item {
            if (layers.isNotEmpty() && selectedLayerIndex in layers.indices) {
                val curLayer = layers[selectedLayerIndex]
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
                            text = "Редактирование слоя",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MildyTextPrimary
                        )

                        OutlinedTextField(
                            value = curLayer.content,
                            onValueChange = { newTxt ->
                                val updated = layers.toMutableList()
                                updated[selectedLayerIndex] = curLayer.copy(content = newTxt)
                                layers = updated
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = MildyTextPrimary,
                                unfocusedTextColor = MildyTextPrimary,
                                focusedBorderColor = MildyBrightRed,
                                unfocusedBorderColor = MildyBorder
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Размер шрифта", color = MildyTextSecondary, fontSize = 12.sp)
                            Slider(
                                value = curLayer.fontSize.toFloat(),
                                onValueChange = { newSize ->
                                    val updated = layers.toMutableList()
                                    updated[selectedLayerIndex] = curLayer.copy(fontSize = newSize.toInt())
                                    layers = updated
                                },
                                valueRange = 14f..64f,
                                modifier = Modifier.weight(1f).padding(horizontal = 12.dp),
                                colors = SliderDefaults.colors(thumbColor = MildyBrightRed, activeTrackColor = MildyBrightRed)
                            )
                            Text("${curLayer.fontSize}px", color = MildyTextMuted, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Layer Ordering List
        item {
            Text(
                text = stringResource(R.string.layers),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MildyTextPrimary
            )
        }

        itemsIndexed(layers) { idx, layer ->
            val isSelected = idx == selectedLayerIndex
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { selectedLayerIndex = idx },
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) MildyBgSurface else MildyBgCard
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${idx + 1}. ${layer.content}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) MildyBrightRed else MildyTextPrimary
                    )

                    IconButton(
                        onClick = {
                            val updated = layers.toMutableList()
                            updated.removeAt(idx)
                            layers = updated
                            if (selectedLayerIndex >= layers.size) selectedLayerIndex = layers.size - 1
                        }
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MildyRed)
                    }
                }
            }
        }

        // Final Export Button
        item {
            Button(
                onClick = { /* Export Cover */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MildyBrightRed),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.Download, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Сохранить обложку (PNG)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
