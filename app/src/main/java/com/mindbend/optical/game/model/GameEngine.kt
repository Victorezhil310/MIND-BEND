package com.mindbend.optical.game.model

import android.content.Context
import android.content.SharedPreferences
import com.mindbend.optical.game.audio.ToneSynthesizer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

enum class GameState {
    START,
    PLAYING,
    GAME_OVER
}

enum class TapFeedback {
    NONE,
    PERFECT,
    GOOD,
    MISS
}

data class Particle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    var radius: Float,
    var colorHex: Long,
    var alpha: Float = 1.0f,
    var lifeSec: Float = 0.6f,
    var maxLifeSec: Float = 0.6f
)

data class Shockwave(
    val x: Float,
    val y: Float,
    var radius: Float = 0f,
    var maxRadius: Float = 120f,
    var alpha: Float = 1.0f,
    val colorHex: Long
)

data class PathNode(
    val id: Int,
    var x: Float,
    var y: Float,
    val direction: Int,
    val length: Float,
    var hasTurnTrigger: Boolean = true,
    var isTurned: Boolean = false,
    val angleDeg: Float = 0f
)

data class MindBendUiState(
    val gameState: GameState = GameState.START,
    val score: Int = 0,
    val tapCount: Int = 0,
    val highestTapCount: Int = 0,
    val highScore: Int = 0,
    val comboStreak: Int = 0,
    val multiplier: Int = 1,
    val illusionType: OpticalIllusionType = OpticalIllusionType.PENROSE_TRIANGLE,
    val colorPalette: ColorPalette = ColorPalette.NEON_CYBER,
    val runnerProgress: Float = 0f,
    val currentSegmentIndex: Int = 0,
    val isToneMuted: Boolean = false,
    val tapFeedback: TapFeedback = TapFeedback.NONE,
    val illusionRotationDeg: Float = 0f,
    val illusionPhase: Float = 0f,
    val perspectiveScale: Float = 1.0f,
    val cameraAngleDeg: Float = 0f,
    val cameraPitchDeg: Float = 0f,
    val screenShake: Float = 0f,
    val particles: List<Particle> = emptyList(),
    val shockwaves: List<Shockwave> = emptyList(),
    val placeName: String = "PENROSE NEXUS",
    val isNewRecord: Boolean = false
)

class GameEngine(private val context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("MIND_BEND_PREFS", Context.MODE_PRIVATE)

    private val _uiState = MutableStateFlow(
        MindBendUiState(
            highestTapCount = prefs.getInt(KEY_HIGHEST_TAPS, 0),
            highScore = prefs.getInt(KEY_HIGH_SCORE, 0)
        )
    )
    val uiState: StateFlow<MindBendUiState> = _uiState.asStateFlow()

    val toneSynthesizer = ToneSynthesizer()

    private val pathNodes = mutableListOf<PathNode>()
    private val activeParticles = mutableListOf<Particle>()
    private val activeShockwaves = mutableListOf<Shockwave>()
    private var speed = 0.009f
    private var targetCameraAngle = 0f
    private var targetCameraPitch = 0f

    init {
        resetPath()
    }

    private fun resetPath() {
        pathNodes.clear()
        var curX = 0.5f
        var curY = 0.8f
        var curDir = 0

        for (i in 0 until 60) {
            val len = 0.22f
            val nodeAngle = (i * 90f) % 360f
            pathNodes.add(
                PathNode(
                    id = i,
                    x = curX,
                    y = curY,
                    direction = curDir,
                    length = len,
                    hasTurnTrigger = i > 0,
                    angleDeg = nodeAngle
                )
            )
            when (curDir) {
                0 -> curY -= len
                1 -> curX += len
                2 -> curY += len
                3 -> curX -= len
            }
            curDir = if (i % 2 == 0) (curDir + 1) % 4 else (curDir + 3) % 4
        }
    }

    fun startGame() {
        resetPath()
        activeParticles.clear()
        activeShockwaves.clear()
        speed = 0.008f
        targetCameraAngle = 0f
        targetCameraPitch = 0f

        val currentHighest = prefs.getInt(KEY_HIGHEST_TAPS, 0)
        val currentHigh = prefs.getInt(KEY_HIGH_SCORE, 0)

        _uiState.value = _uiState.value.copy(
            gameState = GameState.PLAYING,
            score = 0,
            tapCount = 0,
            comboStreak = 0,
            multiplier = 1,
            runnerProgress = 0f,
            currentSegmentIndex = 0,
            highestTapCount = currentHighest,
            highScore = currentHigh,
            tapFeedback = TapFeedback.NONE,
            cameraAngleDeg = 0f,
            cameraPitchDeg = 0f,
            screenShake = 0f,
            placeName = OpticalIllusionType.PENROSE_TRIANGLE.title,
            particles = emptyList(),
            shockwaves = emptyList(),
            isNewRecord = false
        )
    }

    fun updateFrame(deltaTimeSec: Float) {
        val currentState = _uiState.value

        // Particles decay
        val particleIter = activeParticles.iterator()
        while (particleIter.hasNext()) {
            val p = particleIter.next()
            p.lifeSec -= deltaTimeSec
            if (p.lifeSec <= 0f) {
                particleIter.remove()
            } else {
                p.x += p.vx * deltaTimeSec
                p.y += p.vy * deltaTimeSec
                p.alpha = (p.lifeSec / p.maxLifeSec).coerceIn(0f, 1f)
            }
        }

        // Shockwaves expansion
        val shockIter = activeShockwaves.iterator()
        while (shockIter.hasNext()) {
            val s = shockIter.next()
            s.radius += deltaTimeSec * 280f
            s.alpha = (1.0f - (s.radius / s.maxRadius)).coerceIn(0f, 1f)
            if (s.radius >= s.maxRadius) {
                shockIter.remove()
            }
        }

        val currentCamAngle = currentState.cameraAngleDeg + (targetCameraAngle - currentState.cameraAngleDeg) * (deltaTimeSec * 6f)
        val currentCamPitch = currentState.cameraPitchDeg + (targetCameraPitch - currentState.cameraPitchDeg) * (deltaTimeSec * 6f)
        val newShake = (currentState.screenShake - deltaTimeSec * 15f).coerceAtLeast(0f)

        if (currentState.gameState != GameState.PLAYING) {
            val newRot = (currentState.illusionRotationDeg + deltaTimeSec * 35f) % 360f
            val newPhase = (currentState.illusionPhase + deltaTimeSec * 2f) % (2f * Math.PI.toFloat())
            _uiState.value = currentState.copy(
                illusionRotationDeg = newRot,
                illusionPhase = newPhase,
                cameraAngleDeg = currentCamAngle,
                particles = activeParticles.toList(),
                shockwaves = activeShockwaves.toList()
            )
            return
        }

        var newProgress = currentState.runnerProgress + (speed * (1f + currentState.comboStreak * 0.025f))
        var curSegIndex = currentState.currentSegmentIndex
        var isGameOver = false

        val curSeg = pathNodes.getOrNull(curSegIndex)
        if (curSeg != null && curSeg.hasTurnTrigger && !curSeg.isTurned) {
            if (newProgress > 0.88f) {
                isGameOver = true
            }
        }

        if (newProgress >= 1.0f) {
            newProgress -= 1.0f
            curSegIndex++
            if (curSegIndex >= pathNodes.size - 8) {
                extendPath()
            }
        }

        if (isGameOver) {
            triggerGameOver()
            return
        }

        val rotSpeed = 35f + (currentState.tapCount * 0.8f) + (currentState.comboStreak * 2f)
        val newRot = (currentState.illusionRotationDeg + deltaTimeSec * rotSpeed) % 360f
        val newPhase = (currentState.illusionPhase + deltaTimeSec * 2.5f) % (2f * Math.PI.toFloat())
        val perspectiveScale = 1.0f + 0.18f * sin(newPhase.toDouble()).toFloat()

        _uiState.value = currentState.copy(
            runnerProgress = newProgress,
            currentSegmentIndex = curSegIndex,
            score = currentState.score + (1 * currentState.multiplier),
            illusionRotationDeg = newRot,
            illusionPhase = newPhase,
            perspectiveScale = perspectiveScale,
            cameraAngleDeg = currentCamAngle,
            cameraPitchDeg = currentCamPitch,
            screenShake = newShake,
            particles = activeParticles.toList(),
            shockwaves = activeShockwaves.toList()
        )
    }

    fun handleScreenTap() {
        val state = _uiState.value
        if (state.gameState != GameState.PLAYING) return

        val curSegIndex = state.currentSegmentIndex
        val curSeg = pathNodes.getOrNull(curSegIndex) ?: return
        val progress = state.runnerProgress

        val runnerX = curSeg.x
        val runnerY = curSeg.y

        if (curSeg.hasTurnTrigger && !curSeg.isTurned) {
            if (progress in 0.38f..0.85f) {
                curSeg.isTurned = true
                val isPerfect = progress in 0.55f..0.75f
                val addedTaps = state.tapCount + 1
                val newStreak = state.comboStreak + 1
                val newMult = (1 + newStreak / 4).coerceAtMost(10)
                val bonusScore = if (isPerfect) 100 * newMult else 40 * newMult

                targetCameraAngle = (targetCameraAngle + if (Random.nextBoolean()) 45f else -45f)
                targetCameraPitch = sin((addedTaps * 0.5f).toDouble()).toFloat() * 20f

                toneSynthesizer.playTapTone(addedTaps, toneMultiplier = 1.0f + (newStreak % 8) * 0.06f)
                spawnTapExplosion(runnerX, runnerY, isPerfect, state.colorPalette)

                // Auto Illusion / Place Switching every 8 taps!
                val illusionsList = OpticalIllusionType.values()
                val nextIllusion = if (addedTaps % 8 == 0) {
                    illusionsList[(addedTaps / 8) % illusionsList.size]
                } else {
                    state.illusionType
                }

                val paletteList = ColorPalette.values()
                val nextPalette = if (addedTaps % 8 == 0) {
                    paletteList[(addedTaps / 8) % paletteList.size]
                } else {
                    state.colorPalette
                }

                _uiState.value = state.copy(
                    tapCount = addedTaps,
                    comboStreak = newStreak,
                    multiplier = newMult,
                    score = state.score + bonusScore,
                    illusionType = nextIllusion,
                    colorPalette = nextPalette,
                    placeName = nextIllusion.title,
                    screenShake = if (isPerfect) 12f else 6f,
                    tapFeedback = if (isPerfect) TapFeedback.PERFECT else TapFeedback.GOOD
                )
            } else {
                _uiState.value = state.copy(tapFeedback = TapFeedback.MISS)
                triggerGameOver()
            }
        } else {
            val addedTaps = state.tapCount + 1
            targetCameraAngle += 25f
            toneSynthesizer.playTapTone(addedTaps)

            spawnTapExplosion(runnerX, runnerY, false, state.colorPalette)

            _uiState.value = state.copy(
                tapCount = addedTaps,
                score = state.score + 10,
                screenShake = 4f
            )
        }
    }

    private fun spawnTapExplosion(x: Float, y: Float, isPerfect: Boolean, palette: ColorPalette) {
        val numParticles = if (isPerfect) 28 else 16
        val colorHex = if (isPerfect) palette.accent.hashCode().toLong() else palette.primary.hashCode().toLong()

        activeShockwaves.add(
            Shockwave(
                x = x,
                y = y,
                radius = 10f,
                maxRadius = if (isPerfect) 180f else 110f,
                colorHex = colorHex
            )
        )

        for (i in 0 until numParticles) {
            val angle = Random.nextFloat() * 2f * Math.PI.toFloat()
            val speed = Random.nextFloat() * 0.4f + 0.15f
            activeParticles.add(
                Particle(
                    x = x,
                    y = y,
                    vx = cos(angle.toDouble()).toFloat() * speed,
                    vy = sin(angle.toDouble()).toFloat() * speed,
                    radius = Random.nextFloat() * 8f + 4f,
                    colorHex = colorHex,
                    lifeSec = 0.5f + Random.nextFloat() * 0.3f,
                    maxLifeSec = 0.8f
                )
            )
        }
    }

    private fun triggerGameOver() {
        val currentState = _uiState.value
        toneSynthesizer.playGameOverTone()

        var newHighestTap = currentState.highestTapCount
        var newHighScore = currentState.highScore
        var isNewRecord = false

        if (currentState.tapCount > currentState.highestTapCount) {
            newHighestTap = currentState.tapCount
            prefs.edit().putInt(KEY_HIGHEST_TAPS, newHighestTap).apply()
            isNewRecord = true
        }

        if (currentState.score > currentState.highScore) {
            newHighScore = currentState.score
            prefs.edit().putInt(KEY_HIGH_SCORE, newHighScore).apply()
            isNewRecord = true
        }

        _uiState.value = currentState.copy(
            gameState = GameState.GAME_OVER,
            highestTapCount = newHighestTap,
            highScore = newHighScore,
            isNewRecord = isNewRecord,
            screenShake = 18f
        )
    }

    fun selectIllusion(illusionType: OpticalIllusionType) {
        _uiState.value = _uiState.value.copy(illusionType = illusionType, placeName = illusionType.title)
    }

    fun selectPalette(palette: ColorPalette) {
        _uiState.value = _uiState.value.copy(colorPalette = palette)
    }

    fun toggleToneMute(): Boolean {
        val isMuted = toneSynthesizer.toggleMute()
        _uiState.value = _uiState.value.copy(isToneMuted = isMuted)
        return isMuted
    }

    private fun extendPath() {
        val lastNode = pathNodes.last()
        var curX = lastNode.x
        var curY = lastNode.y
        var curDir = lastNode.direction

        for (i in pathNodes.size until pathNodes.size + 25) {
            val len = 0.22f
            val nodeAngle = (i * 90f) % 360f
            pathNodes.add(
                PathNode(
                    id = i,
                    x = curX,
                    y = curY,
                    direction = curDir,
                    length = len,
                    hasTurnTrigger = true,
                    angleDeg = nodeAngle
                )
            )
            curDir = if (Random.nextBoolean()) (curDir + 1) % 4 else (curDir + 3) % 4
            when (curDir) {
                0 -> curY -= len
                1 -> curX += len
                2 -> curY += len
                3 -> curX -= len
            }
        }
    }

    fun getPathNodes(): List<PathNode> = pathNodes

    companion object {
        private const val KEY_HIGHEST_TAPS = "highest_tap_count"
        private const val KEY_HIGH_SCORE = "high_score_val"
    }
}
