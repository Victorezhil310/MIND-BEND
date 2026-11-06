package com.mindbend.optical.game.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import com.mindbend.optical.game.model.ColorPalette
import com.mindbend.optical.game.model.MindBendUiState
import com.mindbend.optical.game.model.OpticalIllusionType
import com.mindbend.optical.game.model.PathNode
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun OpticalIllusionCanvas(
    uiState: MindBendUiState,
    pathNodes: List<PathNode>,
    onTap: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .fillMaxSize()
            .then(
                if (onTap != null && uiState.gameState == com.mindbend.optical.game.model.GameState.PLAYING) {
                    Modifier.pointerInput(Unit) {
                        detectTapGestures(onTap = { onTap() })
                    }
                } else Modifier
            )
    ) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val palette = uiState.colorPalette

        // Screen Shake offset calculation
        val shakeAmount = uiState.screenShake
        val shakeX = if (shakeAmount > 0f) (Random.nextFloat() - 0.5f) * shakeAmount * 2f else 0f
        val shakeY = if (shakeAmount > 0f) (Random.nextFloat() - 0.5f) * shakeAmount * 2f else 0f

        // Apply 3D Camera Angle Transform & Screen Shake to entire canvas!
        withTransform({
            translate(left = shakeX, top = shakeY)
            rotate(degrees = uiState.cameraAngleDeg, pivot = center)
        }) {
            // 1. Draw Optical Illusion Background Pattern
            when (uiState.illusionType) {
                OpticalIllusionType.PENROSE_TRIANGLE -> drawPenroseTriangleIllusion(center, uiState, palette)
                OpticalIllusionType.FRASER_SPIRAL -> drawFraserSpiralIllusion(center, uiState, palette)
                OpticalIllusionType.CAFE_WALL -> drawCafeWallIllusion(size, uiState, palette)
                OpticalIllusionType.MOIRE_SPEEDWAY -> drawMoireSpeedwayIllusion(center, uiState, palette)
                OpticalIllusionType.HERMANN_GRID -> drawHermannGridIllusion(size, uiState, palette)
                OpticalIllusionType.AMES_ROOM -> drawAmesRoomIllusion(size, uiState, palette)
                OpticalIllusionType.INFINITY_TUBE -> drawInfinityTubeIllusion(center, uiState, palette)
            }

            // 2. Draw Auto-Runner Endless Track & Player Avatar
            drawEndlessTrackAndRunner(uiState, pathNodes, palette)

            // 3. Draw Particles & Expanding Shockwaves
            drawParticlesAndShockwaves(uiState, size)
        }
    }
}

private fun DrawScope.drawPenroseTriangleIllusion(
    center: Offset,
    uiState: MindBendUiState,
    palette: ColorPalette
) {
    val rotation = uiState.illusionRotationDeg
    val scaleFactor = uiState.perspectiveScale

    rotate(degrees = rotation, pivot = center) {
        scale(scale = scaleFactor, pivot = center) {
            val sideLength = size.minDimension * 0.42f
            for (layer in 0 until 5) {
                val layerScale = 1f - (layer * 0.18f)
                val len = sideLength * layerScale
                val height = len * (kotlin.math.sqrt(3.0) / 2.0).toFloat()

                val p1 = Offset(center.x, center.y - (2f / 3f) * height)
                val p2 = Offset(center.x - len / 2f, center.y + (1f / 3f) * height)
                val p3 = Offset(center.x + len / 2f, center.y + (1f / 3f) * height)

                val layerColor = if (layer % 2 == 0) palette.primary else palette.secondary

                drawPath(
                    path = Path().apply {
                        moveTo(p1.x, p1.y)
                        lineTo(p2.x, p2.y)
                        lineTo(p3.x, p3.y)
                        close()
                    },
                    color = layerColor.copy(alpha = 0.4f),
                    style = Stroke(width = 14f * (5 - layer))
                )

                val innerOffset = 28f * layerScale
                drawPath(
                    path = Path().apply {
                        moveTo(p1.x, p1.y + innerOffset)
                        lineTo(p2.x + innerOffset, p2.y - innerOffset / 2)
                        lineTo(p3.x - innerOffset, p3.y - innerOffset / 2)
                        close()
                    },
                    color = palette.accent.copy(alpha = 0.6f),
                    style = Stroke(width = 6f)
                )
            }
        }
    }
}

private fun DrawScope.drawFraserSpiralIllusion(
    center: Offset,
    uiState: MindBendUiState,
    palette: ColorPalette
) {
    val rotation = uiState.illusionRotationDeg
    val maxRadius = size.minDimension * 0.48f

    rotate(degrees = rotation, pivot = center) {
        for (r in 3..14) {
            val radius = maxRadius * (r / 14f)
            val numSegments = r * 8
            val circleColor = if (r % 2 == 0) palette.primary else palette.secondary

            drawCircle(
                color = circleColor.copy(alpha = 0.3f),
                radius = radius,
                center = center,
                style = Stroke(width = 5f)
            )

            for (s in 0 until numSegments) {
                val angle = (2.0 * Math.PI * s / numSegments)
                val tiltAngle = angle + Math.toRadians(25.0)

                val x1 = center.x + (radius - 14f) * cos(angle).toFloat()
                val y1 = center.y + (radius - 14f) * sin(angle).toFloat()

                val x2 = center.x + (radius + 14f) * cos(tiltAngle).toFloat()
                val y2 = center.y + (radius + 14f) * sin(tiltAngle).toFloat()

                drawLine(
                    color = if (s % 2 == 0) palette.primary else palette.accent,
                    start = Offset(x1, y1),
                    end = Offset(x2, y2),
                    strokeWidth = 3.5f
                )
            }
        }
    }
}

private fun DrawScope.drawCafeWallIllusion(
    canvasSize: Size,
    uiState: MindBendUiState,
    palette: ColorPalette
) {
    val phase = uiState.illusionPhase
    val numRows = 12
    val rowHeight = canvasSize.height / numRows
    val tileSize = 75f

    for (row in 0 until numRows) {
        val y = row * rowHeight
        val rowOffset = (sin((row * 0.8f + phase).toDouble()).toFloat() * 40f) + ((row % 2) * (tileSize / 2f))

        drawLine(
            color = palette.primary,
            start = Offset(0f, y),
            end = Offset(canvasSize.width, y),
            strokeWidth = 6f
        )

        var x = -tileSize + (rowOffset % tileSize)
        var tileIndex = 0
        while (x < canvasSize.width + tileSize) {
            val isDark = tileIndex % 2 == 0
            val fillColor = if (isDark) palette.background else palette.trackColor

            drawRect(
                color = fillColor,
                topLeft = Offset(x, y + 3f),
                size = Size(tileSize - 3f, rowHeight - 6f)
            )

            x += tileSize
            tileIndex++
        }
    }
}

private fun DrawScope.drawMoireSpeedwayIllusion(
    center: Offset,
    uiState: MindBendUiState,
    palette: ColorPalette
) {
    val phase = uiState.illusionPhase
    val rot1 = uiState.illusionRotationDeg
    val rot2 = -uiState.illusionRotationDeg * 1.6f

    rotate(degrees = rot1, pivot = center) {
        for (i in 0 until 48) {
            val angle = (2.0 * Math.PI * i / 48)
            val endX = center.x + size.width * cos(angle).toFloat()
            val endY = center.y + size.height * sin(angle).toFloat()
            drawLine(
                color = palette.primary.copy(alpha = 0.35f),
                start = center,
                end = Offset(endX, endY),
                strokeWidth = 3f
            )
        }
    }

    rotate(degrees = rot2, pivot = center) {
        for (i in 0 until 48) {
            val angle = (2.0 * Math.PI * i / 48) + (0.06 * sin(phase.toDouble()))
            val endX = center.x + size.width * cos(angle).toFloat()
            val endY = center.y + size.height * sin(angle).toFloat()
            drawLine(
                color = palette.secondary.copy(alpha = 0.4f),
                start = center,
                end = Offset(endX, endY),
                strokeWidth = 3f
            )
        }
    }
}

private fun DrawScope.drawHermannGridIllusion(
    canvasSize: Size,
    uiState: MindBendUiState,
    palette: ColorPalette
) {
    val phase = uiState.illusionPhase
    val gridSpacing = 95f
    val bandWidth = 26f

    drawRect(color = palette.background, topLeft = Offset.Zero, size = canvasSize)

    var x = 40f
    val xPositions = mutableListOf<Float>()
    while (x < canvasSize.width) {
        xPositions.add(x)
        drawRect(
            color = palette.trackColor.copy(alpha = 0.85f),
            topLeft = Offset(x, 0f),
            size = Size(bandWidth, canvasSize.height)
        )
        x += gridSpacing
    }

    var y = 40f
    val yPositions = mutableListOf<Float>()
    while (y < canvasSize.height) {
        yPositions.add(y)
        drawRect(
            color = palette.trackColor.copy(alpha = 0.85f),
            topLeft = Offset(0f, y),
            size = Size(canvasSize.width, bandWidth)
        )
        y += gridSpacing
    }

    for (ix in xPositions) {
        for (iy in yPositions) {
            val dotRadius = 7f + 4f * sin((ix + iy + phase * 10f).toDouble()).toFloat()
            drawCircle(
                color = palette.primary,
                radius = dotRadius,
                center = Offset(ix + bandWidth / 2f, iy + bandWidth / 2f)
            )
        }
    }
}

private fun DrawScope.drawAmesRoomIllusion(
    canvasSize: Size,
    uiState: MindBendUiState,
    palette: ColorPalette
) {
    val center = Offset(canvasSize.width / 2f, canvasSize.height / 2f)
    val scale = uiState.perspectiveScale

    val corners = listOf(
        Offset(0f, 0f),
        Offset(canvasSize.width, 0f),
        Offset(canvasSize.width, canvasSize.height),
        Offset(0f, canvasSize.height)
    )

    for (corner in corners) {
        drawLine(
            color = palette.secondary.copy(alpha = 0.45f),
            start = center,
            end = corner,
            strokeWidth = 4f
        )
    }

    for (depth in 1..6) {
        val dScale = (depth / 6.0f) * scale
        val w = canvasSize.width * 0.42f * dScale
        val h = canvasSize.height * 0.42f * dScale

        val rectPath = Path().apply {
            moveTo(center.x - w, center.y - h * 1.25f)
            lineTo(center.x + w, center.y - h * 0.75f)
            lineTo(center.x + w, center.y + h * 0.75f)
            lineTo(center.x - w, center.y + h * 1.25f)
            close()
        }

        drawPath(
            path = rectPath,
            color = if (depth % 2 == 0) palette.primary.copy(alpha = 0.35f) else palette.accent.copy(alpha = 0.35f),
            style = Stroke(width = 3.5f)
        )
    }
}

private fun DrawScope.drawInfinityTubeIllusion(
    center: Offset,
    uiState: MindBendUiState,
    palette: ColorPalette
) {
    val rot = uiState.illusionRotationDeg
    val phase = uiState.illusionPhase

    rotate(degrees = rot, pivot = center) {
        for (i in 1..10) {
            val ringRadius = size.minDimension * 0.05f * i * (1f + 0.1f * sin((phase + i).toDouble()).toFloat())
            val sides = 8
            val polygonPath = Path()

            for (s in 0 until sides) {
                val angle = (2.0 * Math.PI * s / sides)
                val px = center.x + ringRadius * cos(angle).toFloat()
                val py = center.y + ringRadius * sin(angle).toFloat()
                if (s == 0) polygonPath.moveTo(px, py) else polygonPath.lineTo(px, py)
            }
            polygonPath.close()

            val ringColor = if (i % 3 == 0) palette.accent else if (i % 2 == 0) palette.primary else palette.secondary
            drawPath(
                path = polygonPath,
                color = ringColor.copy(alpha = 0.45f),
                style = Stroke(width = 4f)
            )
        }
    }
}

private fun DrawScope.drawEndlessTrackAndRunner(
    uiState: MindBendUiState,
    pathNodes: List<PathNode>,
    palette: ColorPalette
) {
    if (pathNodes.isEmpty()) return

    val canvasW = size.width
    val canvasH = size.height

    val curIndex = uiState.currentSegmentIndex
    val progress = uiState.runnerProgress

    val currentSeg = pathNodes.getOrNull(curIndex) ?: pathNodes.first()
    val nextSeg = pathNodes.getOrNull(curIndex + 1) ?: currentSeg

    val startPt = Offset(currentSeg.x * canvasW, currentSeg.y * canvasH)
    val endPt = Offset(nextSeg.x * canvasW, nextSeg.y * canvasH)

    val runnerPos = Offset(
        startPt.x + (endPt.x - startPt.x) * progress,
        startPt.y + (endPt.y - startPt.y) * progress
    )

    val trackPath = Path()
    var isFirst = true
    val viewWindowMin = (curIndex - 2).coerceAtLeast(0)
    val viewWindowMax = (curIndex + 10).coerceAtMost(pathNodes.size - 1)

    for (i in viewWindowMin..viewWindowMax) {
        val node = pathNodes[i]
        val pt = Offset(node.x * canvasW, node.y * canvasH)
        if (isFirst) {
            trackPath.moveTo(pt.x, pt.y)
            isFirst = false
        } else {
            trackPath.lineTo(pt.x, pt.y)
        }
    }

    // Outer track glow
    drawPath(
        path = trackPath,
        color = palette.accent.copy(alpha = 0.45f),
        style = Stroke(width = 34f, cap = StrokeCap.Round)
    )

    // Main track path
    drawPath(
        path = trackPath,
        color = palette.primary,
        style = Stroke(width = 18f, cap = StrokeCap.Round)
    )

    // Turn Trigger Target Rings
    for (i in viewWindowMin..viewWindowMax) {
        val node = pathNodes[i]
        if (node.hasTurnTrigger) {
            val pt = Offset(node.x * canvasW, node.y * canvasH)
            val ringColor = if (node.isTurned) palette.accent else palette.secondary
            val ringRadius = if (i == curIndex) 30f else 20f

            drawCircle(
                color = ringColor,
                radius = ringRadius,
                center = pt,
                style = Stroke(width = 7f)
            )

            if (i == curIndex && !node.isTurned) {
                drawCircle(
                    color = palette.accent.copy(alpha = 0.7f),
                    radius = ringRadius + (progress * 18f),
                    center = pt,
                    style = Stroke(width = 5f)
                )
            }
        }
    }

    // Draw Player Energy Orb Avatar
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(palette.secondary, Color.Transparent),
            center = runnerPos,
            radius = 50f
        ),
        radius = 50f,
        center = runnerPos
    )

    drawCircle(
        color = palette.accent,
        radius = 18f,
        center = runnerPos
    )
    drawCircle(
        color = Color.White,
        radius = 9f,
        center = runnerPos
    )
}

private fun DrawScope.drawParticlesAndShockwaves(uiState: MindBendUiState, canvasSize: Size) {
    val canvasW = canvasSize.width
    val canvasH = canvasSize.height

    // Draw expanding shockwaves
    for (s in uiState.shockwaves) {
        val pt = Offset(s.x * canvasW, s.y * canvasH)
        drawCircle(
            color = Color(s.colorHex).copy(alpha = s.alpha),
            radius = s.radius,
            center = pt,
            style = Stroke(width = 6f)
        )
    }

    // Draw particles
    for (p in uiState.particles) {
        val pt = Offset(p.x * canvasW, p.y * canvasH)
        drawCircle(
            color = Color(p.colorHex).copy(alpha = p.alpha),
            radius = p.radius,
            center = pt
        )
    }
}
