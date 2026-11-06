package com.mindbend.optical.game

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import com.mindbend.optical.game.model.GameEngine
import com.mindbend.optical.game.model.GameState
import com.mindbend.optical.game.ui.*

class MainActivity : ComponentActivity() {

    private lateinit var gameEngine: GameEngine

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        gameEngine = GameEngine(applicationContext)

        setContent {
            MindBendTheme {
                val uiState by gameEngine.uiState.collectAsState()
                var showGallery by remember { mutableStateOf(false) }

                // Frame loop for smooth 60fps canvas animation & runner physics
                LaunchedEffect(Unit) {
                    var lastTime = System.nanoTime()
                    while (true) {
                        val now = System.nanoTime()
                        val deltaSec = ((now - lastTime) / 1_000_000_000f).coerceAtMost(0.05f)
                        lastTime = now

                        gameEngine.updateFrame(deltaSec)
                        withFrameNanos { }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(uiState.colorPalette.background)
                        .then(
                            if (uiState.gameState == GameState.PLAYING && !showGallery) {
                                Modifier.pointerInput(Unit) {
                                    detectTapGestures(
                                        onTap = { gameEngine.handleScreenTap() }
                                    )
                                }
                            } else Modifier
                        )
                ) {
                    // Background Optical Illusion Canvas (active on all screens)
                    OpticalIllusionCanvas(
                        uiState = uiState,
                        pathNodes = gameEngine.getPathNodes(),
                        modifier = Modifier.fillMaxSize()
                    )

                    if (showGallery) {
                        IllusionGalleryScreen(
                            uiState = uiState,
                            onSelectIllusion = { gameEngine.selectIllusion(it) },
                            onSelectPalette = { gameEngine.selectPalette(it) },
                            onBack = { showGallery = false }
                        )
                    } else {
                        when (uiState.gameState) {
                            GameState.START -> {
                                StartScreen(
                                    uiState = uiState,
                                    onStartGame = { gameEngine.startGame() },
                                    onSelectIllusion = { gameEngine.selectIllusion(it) },
                                    onSelectPalette = { gameEngine.selectPalette(it) },
                                    onToggleMute = { gameEngine.toggleToneMute() }
                                )
                            }
                            GameState.PLAYING -> {
                                GameScreen(
                                    uiState = uiState,
                                    onToggleMute = { gameEngine.toggleToneMute() }
                                )
                            }
                            GameState.GAME_OVER -> {
                                GameOverScreen(
                                    uiState = uiState,
                                    onPlayAgain = { gameEngine.startGame() },
                                    onOpenGallery = { showGallery = true }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
