package com.mindbend.optical.game.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mindbend.optical.game.model.ColorPalette
import com.mindbend.optical.game.model.MindBendUiState
import com.mindbend.optical.game.model.OpticalIllusionType

@Composable
fun StartScreen(
    uiState: MindBendUiState,
    onStartGame: () -> Unit,
    onSelectIllusion: (OpticalIllusionType) -> Unit,
    onSelectPalette: (ColorPalette) -> Unit,
    onToggleMute: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showHowToPlay by remember { mutableStateOf(false) }

    // Pulsating animation for "TAP TO START"
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scalePulse by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.45f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar: Mute button & How to Play
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onToggleMute,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color(0xFF1F2338))
                ) {
                    Icon(
                        imageVector = if (uiState.isToneMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                        contentDescription = "Toggle Audio Tones",
                        tint = uiState.colorPalette.primary
                    )
                }

                IconButton(
                    onClick = { showHowToPlay = true },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color(0xFF1F2338))
                ) {
                    Icon(
                        imageVector = Icons.Default.HelpOutline,
                        contentDescription = "How to Play",
                        tint = uiState.colorPalette.accent
                    )
                }
            }

            // Title Banner
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "MIND BEND",
                    fontSize = 46.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = 4.sp
                )

                Text(
                    text = "OPTICAL ILLUSION RUNNER",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = uiState.colorPalette.primary,
                    letterSpacing = 2.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )

                // Highest Tap & High Score Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF171A29).copy(alpha = 0.9f)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .border(1.dp, uiState.colorPalette.primary.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "HIGHEST TAP",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )
                            Text(
                                text = "${uiState.highestTapCount}",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = uiState.colorPalette.accent
                            )
                        }

                        Divider(
                            modifier = Modifier
                                .height(36.dp)
                                .width(1.dp),
                            color = Color.Gray.copy(alpha = 0.3f)
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "BEST SCORE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )
                            Text(
                                text = "${uiState.highScore}",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = uiState.colorPalette.primary
                            )
                        }
                    }
                }
            }

            // Optical Illusion Selector Carousel
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "SELECT OPTICAL ILLUSION",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.7f),
                    letterSpacing = 1.5.sp,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    items(OpticalIllusionType.values()) { illusion ->
                        val isSelected = uiState.illusionType == illusion
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    if (isSelected) uiState.colorPalette.primary.copy(alpha = 0.25f)
                                    else Color(0xFF1E2436).copy(alpha = 0.8f)
                                )
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) uiState.colorPalette.primary else Color.Transparent,
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .clickable { onSelectIllusion(illusion) }
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = illusion.title,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else Color.LightGray
                                )
                                Text(
                                    text = illusion.difficulty,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = when (illusion.difficulty) {
                                        "NORMAL" -> Color(0xFF76FF03)
                                        "HARD" -> Color(0xFFFFD700)
                                        "EXPERT" -> Color(0xFFFF9100)
                                        else -> Color(0xFFFF1744)
                                    }
                                )
                            }
                        }
                    }
                }

                // Color Palette Switcher
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    ColorPalette.values().forEach { palette ->
                        val isSelected = uiState.colorPalette == palette
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(palette.primary)
                                .border(
                                    width = if (isSelected) 3.dp else 0.dp,
                                    color = Color.White,
                                    shape = CircleShape
                                )
                                .clickable { onSelectPalette(palette) }
                        )
                    }
                }
            }

            // Start Button
            Button(
                onClick = onStartGame,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(0.dp),
                shape = RoundedCornerShape(30.dp),
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(64.dp)
                    .scale(scalePulse)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(uiState.colorPalette.primary, uiState.colorPalette.secondary)
                            ),
                            shape = RoundedCornerShape(30.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Start",
                            tint = Color.Black,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "TAP TO RUN",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.Black,
                            letterSpacing = 2.sp
                        )
                    }
                }
            }
        }

        // How To Play Dialog
        if (showHowToPlay) {
            AlertDialog(
                onDismissRequest = { showHowToPlay = false },
                containerColor = Color(0xFF171A29),
                title = {
                    Text(
                        text = "HOW TO PLAY",
                        color = uiState.colorPalette.primary,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("1. Auto-Runner: Your orb constantly moves forward along shifting optical illusion tracks.")
                        Text("2. Tap to Turn: Tap the screen when approaching yellow turn circles on the track.")
                        Text("3. Tap to Tone: Every tap plays a soothing pentatonic synth chime and changes tone state.")
                        Text("4. Endless Illusions: Survive as tracks bend, shift perspective, and accelerate infinitely!")
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showHowToPlay = false }) {
                        Text("GOT IT!", color = uiState.colorPalette.accent, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    }
}
