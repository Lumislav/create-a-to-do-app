package com.palimson.mildy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.palimson.mildy.R
import com.palimson.mildy.navigation.Screen
import com.palimson.mildy.ui.theme.*

data class ToolItemData(
    val titleRes: Int,
    val descRes: Int,
    val icon: ImageVector,
    val targetRoute: String
)

@Composable
fun ToolsScreen(
    onNavigate: (String) -> Unit
) {
    val tools = listOf(
        ToolItemData(
            titleRes = R.string.tool_subtitles,
            descRes = R.string.tool_subtitles_desc,
            icon = Icons.Default.ClosedCaption,
            targetRoute = Screen.Subtitles.route
        ),
        ToolItemData(
            titleRes = R.string.tool_photofix,
            descRes = R.string.tool_photofix_desc,
            icon = Icons.Default.AutoFixHigh,
            targetRoute = Screen.PhotoFix.route
        ),
        ToolItemData(
            titleRes = R.string.tool_video_tools,
            descRes = R.string.tool_video_tools_desc,
            icon = Icons.Default.Videocam,
            targetRoute = Screen.VideoTools.route
        ),
        ToolItemData(
            titleRes = R.string.tool_thumbnail,
            descRes = R.string.tool_thumbnail_desc,
            icon = Icons.Default.Image,
            targetRoute = Screen.Thumbnail.route
        ),
        ToolItemData(
            titleRes = R.string.tool_resizer,
            descRes = R.string.tool_photofix_desc,
            icon = Icons.Default.Crop,
            targetRoute = Screen.Utility.route
        ),
        ToolItemData(
            titleRes = R.string.tool_compressor,
            descRes = R.string.tool_video_tools_desc,
            icon = Icons.Default.Compress,
            targetRoute = Screen.Utility.route
        ),
        ToolItemData(
            titleRes = R.string.tool_watermark,
            descRes = R.string.tool_photofix_desc,
            icon = Icons.Default.Shield,
            targetRoute = Screen.Utility.route
        ),
        ToolItemData(
            titleRes = R.string.tool_color_picker,
            descRes = R.string.tool_photofix_desc,
            icon = Icons.Default.Colorize,
            targetRoute = Screen.Utility.route
        )
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MildyBgPrimary)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.all_tools),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MildyTextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        items(tools) { tool ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onNavigate(tool.targetRoute) },
                colors = CardDefaults.cardColors(containerColor = MildyBgCard),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MildyBgSurface),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = tool.icon,
                            contentDescription = null,
                            tint = MildyBrightRed,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(id = tool.titleRes),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MildyTextPrimary
                        )
                        Text(
                            text = stringResource(id = tool.descRes),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MildyTextMuted,
                            maxLines = 2
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = MildyTextMuted
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
