# 🌀 MIND BEND - Fullscreen Optical Illusion Endless Runner

![MIND BEND Logo](https://mind-bend-five.vercel.app/og-image.png)

[![Live Game Preview](https://img.shields.io/badge/Live%20Preview-Vercel-00E5FF?style=for-the-badge&logo=vercel)](https://mind-bend-five.vercel.app/)
[![Google Play Store](https://img.shields.io/badge/Target%20SDK-34%20(Android%2014)-76FF03?style=for-the-badge&logo=android)](https://mind-bend-five.vercel.app/)
[![License](https://img.shields.io/badge/License-MIT-FF007A?style=for-the-badge)](LICENSE)

**MIND BEND** is an infinite 60FPS optical illusion auto-runner game with tap-to-turn mechanics, dynamic 3D camera shifts, procedural biome transitions, and real-time pentatonic Web Audio synth chimes.

---

## 🎮 Game Features & Mechanics

- **🌀 8 Dynamic Optical Illusion Biomes ("Places")**:
  1. **PENROSE NEXUS**: Impossible 3D geometric loop staircase.
  2. **FRASER VORTEX**: Concentric false-vortex optical rings.
  3. **CAFÉ WALL CORRIDOR**: Tilting checkerboard perspective corridor.
  4. **MOIRÉ KALEIDOSCOPE**: Overlapping line grids creating kinetic wave ripples.
  5. **HERMANN SCINTILLATION MATRIX**: High-contrast grid with scintillating phantom flash dots.
  6. **AMES PERSPECTIVE HALLWAY**: Trapezoid depth room scaling.
  7. **INFINITY HYPER-TUBE**: Hyperspace polygon tunnel rings.
  8. **QUANTUM PRISM VOID**: Multi-spectrum rainbow geometry.

- **⚡ Tactile Tap-To-Turn Controls**:
  - Auto-running orb along procedural optical tracks.
  - Tap screen to turn direction at target nodes, trigger 360° particle bursts, expanding shockwave rings, and 3D camera angle tilts (`45°` / `-45°`).

- **🎵 Tap-To-Tone Audio Synthesizer**:
  - Built-in Web Audio API & Android `AudioTrack` acoustic synthesizer.
  - Every tap generates soothing pentatonic synth bell chimes (`C4`–`E5` scale with pitch multipliers).

- **🌈 Continuous Rainbow Color Morphing**:
  - HSL spectrum hue rotation dynamically cycles color palettes (`Neon Cyber`, `Monochrome`, `Pastel Twilight`, `Solarized`, `Vaporwave`).

---

## 🚀 Live Demo & Policy Endpoints

- **🌐 Live Web Game**: [https://mind-bend-five.vercel.app/](https://mind-bend-five.vercel.app/)
- **🔒 Privacy Policy**: [https://mind-bend-five.vercel.app/privacy](https://mind-bend-five.vercel.app/privacy)
- **📄 Terms of Service**: [https://mind-bend-five.vercel.app/terms](https://mind-bend-five.vercel.app/terms)
- **💰 Google AdSense `ads.txt`**: [https://mind-bend-five.vercel.app/ads.txt](https://mind-bend-five.vercel.app/ads.txt)
- **🗺️ XML Sitemap**: [https://mind-bend-five.vercel.app/sitemap.xml](https://mind-bend-five.vercel.app/sitemap.xml)

---

## 📲 Android Project Architecture

- **Language & UI**: 100% Kotlin with Jetpack Compose & Hardware-Accelerated Vector Canvas.
- **Target SDK**: `34` (Android 14).
- **Min SDK**: `24` (Android 7.0+).
- **Application ID**: `com.mindbend.optical.game`
- **Firebase Integration**: `com.google.gms.google-services` plugin & Firebase BoM (`33.1.0`).

---

## 🛠️ Build Commands

### Rebuild Android Release Binaries (.aab / .apk)
```bash
./gradlew assembleRelease bundleRelease
```

### Run Local Web Server (Port 3464)
```bash
python -m http.server 3464 --bind 127.0.0.1
```

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
