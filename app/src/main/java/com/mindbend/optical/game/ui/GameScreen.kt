package com.mindbend.optical.game.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mindbend.optical.game.model.MindBendUiState
import com.mindbend.optical.game.model.TapFeedback

@Composable
fun GameScreen(
    uiState: MindBendUiState,
    onToggleMute: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        // Top HUD Overlay
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp, start = 20.dp, end = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            // Score & Taps Pill
            Column {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF121626).copy(alpha = 0.85f)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Column {
                            Text(
                                text = "SCORE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )
                            Text(
                                text = "${uiState.score}",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }

                        Column {
                            Text(
                                text = "TAPS",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )
                            Text(
                                text = "${uiState.tapCount}",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                color = uiState.colorPalette.accent
                            )
                        }
                    }
                }

                // Combo streak multiplier badge
                if (uiState.comboStreak > 1) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = uiState.colorPalette.secondary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text(
                            text = "${uiState.multiplier}x MULTIPLIER (${uiState.comboStreak} STREAK)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Right side: Audio Mute & Location / Illusion badge
            Column(horizontalAlignment = Alignment.End) {
                IconButton(
                    onClick = onToggleMute,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color(0xFF121626).copy(alpha = 0.85f))
                ) {
                    Icon(
                        imageVector = if (uiState.isToneMuted) Icons.AutoMirrored.Filled.VolumeOff else Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Mute Tones",
                        tint = uiState.colorPalette.primary
                    )
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF121626).copy(alpha = 0.85f)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text(
                        text = uiState.placeName,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = uiState.colorPalette.primary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // Tap Feedback Center Banner
        AnimatedVisibility(
            visible = uiState.tapFeedback != TapFeedback.NONE,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            when (uiState.tapFeedback) {
                TapFeedback.PERFECT -> {
                    Text(
                        text = "PERFECT TURN!",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = uiState.colorPalette.accent
                    )
                }
                TapFeedback.GOOD -> {
                    Text(
                        text = "TURN!",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = uiState.colorPalette.primary
                    )
                }
                TapFeedback.MISS -> {
                    Text(
                        text = "MISSED TURN!",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Red
                    )
                }
                else -> {}
            }
        }
    }
}
