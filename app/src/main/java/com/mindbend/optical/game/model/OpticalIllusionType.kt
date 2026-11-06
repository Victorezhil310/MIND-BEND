package com.mindbend.optical.game.model

import androidx.compose.ui.graphics.Color

enum class OpticalIllusionType(
    val title: String,
    val subtitle: String,
    val description: String,
    val difficulty: String
) {
    PENROSE_TRIANGLE(
        title = "Penrose Loop",
        subtitle = "Impossible Geometric Path",
        description = "Navigate an impossible 3D geometric staircase where loops warp perspective endlessly.",
        difficulty = "NORMAL"
    ),
    FRASER_SPIRAL(
        title = "Fraser Spiral",
        subtitle = "Concentric False Vortex",
        description = "Concentric circles that visually trick your brain into seeing an infinite spiral.",
        difficulty = "HARD"
    ),
    CAFE_WALL(
        title = "Café Wall",
        subtitle = "Parallel Tilting Grid",
        description = "Offset checkerboard patterns that cause straight parallel tracks to visually slope.",
        difficulty = "EXPERT"
    ),
    MOIRE_SPEEDWAY(
        title = "Moiré Speedway",
        subtitle = "Kinetic Interference Motion",
        description = "Overlapping line grids generating optical speed waves that warp target trajectory.",
        difficulty = "INSANE"
    ),
    HERMANN_GRID(
        title = "Hermann Matrix",
        subtitle = "Scintillating Phantom Grid",
        description = "High-contrast geometric intersections flashing phantom dots as velocity ramps up.",
        difficulty = "HARD"
    ),
    AMES_ROOM(
        title = "Ames Perspective",
        subtitle = "Distorted Room Scale",
        description = "Room perspective dynamically expands and contracts altering timing windows.",
        difficulty = "EXPERT"
    ),
    INFINITY_TUBE(
        title = "Infinity Tube",
        subtitle = "Hyper-Space Vortex Tunnel",
        description = "Hyper-dimensional polygon tunnel rings spinning in multi-axis depth loops.",
        difficulty = "INSANE"
    )
}

enum class ColorPalette(
    val title: String,
    val primary: Color,
    val secondary: Color,
    val accent: Color,
    val background: Color,
    val trackColor: Color
) {
    NEON_CYBER(
        title = "Neon Cyber",
        primary = Color(0xFF00E5FF),
        secondary = Color(0xFFFF007A),
        accent = Color(0xFF76FF03),
        background = Color(0xFF0A0D1A),
        trackColor = Color(0xFF1E2640)
    ),
    MONOCHROME_HYPNOSIS(
        title = "Monochrome",
        primary = Color(0xFFFFFFFF),
        secondary = Color(0xFFA0A0A0),
        accent = Color(0xFFFFD700),
        background = Color(0xFF000000),
        trackColor = Color(0xFF2B2B2B)
    ),
    PASTEL_TWILIGHT(
        title = "Pastel Twilight",
        primary = Color(0xFFFF9EAA),
        secondary = Color(0xFFA3E4D7),
        accent = Color(0xFFF9E79F),
        background = Color(0xFF1A1528),
        trackColor = Color(0xFF32284C)
    ),
    SOLARIZED_FLASH(
        title = "Solarized Flash",
        primary = Color(0xFFFFAB00),
        secondary = Color(0xFFFF3D00),
        accent = Color(0xFF00E676),
        background = Color(0xFF120024),
        trackColor = Color(0xFF3D005E)
    ),
    VAPORWAVE_LOOPS(
        title = "Vaporwave",
        primary = Color(0xFFFF71CE),
        secondary = Color(0xFF01CDFE),
        accent = Color(0xFF05FFA1),
        background = Color(0xFF110826),
        trackColor = Color(0xFF2B1B4D)
    )
}
