package com.palimson.mildy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.NetworkCheck
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
fun SettingsScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val settings by viewModel.settings.collectAsState()
    var backendUrlInput by remember(settings.backendUrl) { mutableStateOf(settings.backendUrl) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MildyBgPrimary)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
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
                        text = stringResource(R.string.nav_settings),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MildyTextPrimary
                    )
                }
            }
        }

        // Appearance & Language Group
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MildyBgCard),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Theme Switcher
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = stringResource(R.string.settings_theme),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MildyTextPrimary
                            )
                            Text(
                                text = if (settings.isDarkTheme) stringResource(R.string.settings_theme_dark) else stringResource(R.string.settings_theme_light),
                                style = MaterialTheme.typography.bodySmall,
                                color = MildyTextMuted
                            )
                        }

                        Switch(
                            checked = settings.isDarkTheme,
                            onCheckedChange = { viewModel.setTheme(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = MildyBrightRed
                            )
                        )
                    }

                    Divider(color = MildyBorder)

                    // Language Selector
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = stringResource(R.string.settings_language),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MildyTextPrimary
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val langs = listOf(
                                Triple("kk", "Қазақша", "KZ"),
                                Triple("ru", "Русский", "RU"),
                                Triple("en", "English", "EN")
                            )

                            langs.forEach { (code, label, flag) ->
                                val isSelected = settings.languageCode == code
                                Button(
                                    onClick = { viewModel.setLanguage(code) },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isSelected) MildyBrightRed else MildyBgSurface
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(vertical = 10.dp)
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) Color.White else MildyTextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // AI Engine & Server Configuration Group
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MildyBgCard),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = stringResource(R.string.settings_ai_mode),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MildyTextPrimary
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.setAiMode("local") },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (settings.aiMode == "local") MildyBrightRed else MildyBgSurface
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "On-Device",
                                fontSize = 12.sp,
                                color = if (settings.aiMode == "local") Color.White else MildyTextSecondary
                            )
                        }

                        Button(
                            onClick = { viewModel.setAiMode("server") },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (settings.aiMode == "server") MildyBrightRed else MildyBgSurface
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "FastAPI Сервер",
                                fontSize = 12.sp,
                                color = if (settings.aiMode == "server") Color.White else MildyTextSecondary
                            )
                        }
                    }

                    // Server URL Input
                    OutlinedTextField(
                        value = backendUrlInput,
                        onValueChange = {
                            backendUrlInput = it
                            viewModel.setBackendUrl(it)
                        },
                        label = { Text(stringResource(R.string.settings_backend_url)) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MildyBrightRed,
                            unfocusedBorderColor = MildyBorder,
                            focusedTextColor = MildyTextPrimary,
                            unfocusedTextColor = MildyTextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Button(
                        onClick = { viewModel.checkBackendHealth() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MildyBgSurface),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.NetworkCheck, contentDescription = null, tint = MildyBrightRed)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.settings_test_connection),
                            color = MildyTextPrimary,
                            fontSize = 13.sp
                        )
                    }

                    // Status Pill
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (settings.isServerOnline) Color(0x2600FF66) else MildyBgSurface)
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (settings.isServerOnline) Color(0xFF00FF66) else Color(0xFFFF9900))
                        )
                        Text(
                            text = settings.serverStatusMessage,
                            style = MaterialTheme.typography.bodySmall,
                            color = MildyTextPrimary
                        )
                    }
                }
            }
        }

        // About & PALIMSON Group
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MildyBgCard),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Mildy by PALIMSON Group",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MildyTextPrimary
                    )
                    Text(
                        text = stringResource(R.string.version_info),
                        style = MaterialTheme.typography.bodySmall,
                        color = MildyTextMuted
                    )
                    Text(
                        text = stringResource(R.string.google_play_ready),
                        style = MaterialTheme.typography.bodySmall,
                        color = MildyBrightRed
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
