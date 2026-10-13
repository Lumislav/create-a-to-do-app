package com.palimson.mildy.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.palimson.mildy.R
import com.palimson.mildy.ui.theme.*

@Composable
fun MildyTopBar(
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MildyBgPrimary)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(MildyBrightRed, MildyRed, MildyBurgundy)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "M",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp
                )
            }

            Column {
                Text(
                    text = stringResource(id = R.string.app_name),
                    style = MaterialTheme.typography.titleLarge,
                    color = MildyTextPrimary
                )
                Text(
                    text = stringResource(id = R.string.company_name).uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MildyTextMuted,
                    letterSpacing = 1.sp
                )
            }
        }

        IconButton(
            onClick = onSettingsClick,
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(MildyBgCard)
        ) {
            Icon(
                imageVector = Icons.Default.Tune,
                contentDescription = "Settings",
                tint = MildyTextPrimary
            )
        }
    }
}

@Composable
fun MildyBottomBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        containerColor = MildyBgSecondary,
        contentColor = MildyTextPrimary,
        tonalElevation = 8.dp,
        modifier = Modifier.height(68.dp)
    ) {
        val items = listOf(
            Triple("home", Icons.Default.Home, R.string.nav_home),
            Triple("tools", Icons.Default.Widgets, R.string.nav_tools),
            Triple("projects", Icons.Default.Folder, R.string.nav_projects),
            Triple("settings", Icons.Default.Settings, R.string.nav_settings)
        )

        items.forEach { (route, icon, labelRes) ->
            val isSelected = currentRoute == route
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(route) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isSelected) MildyBrightRed else MildyTextMuted
                    )
                },
                label = {
                    Text(
                        text = stringResource(id = labelRes),
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) MildyBrightRed else MildyTextMuted
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

@Composable
fun QuickToolCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    gradient: List<Color>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MildyBgCard),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Brush.linearGradient(gradient)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MildyTextPrimary
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MildyTextMuted
                )
            }
        }
    }
}
