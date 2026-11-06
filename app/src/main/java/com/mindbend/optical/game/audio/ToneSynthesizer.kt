package com.mindbend.optical.game.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.sin

/**
 * Real-time Audio Synthesizer generating tap-to-tone harmonic frequencies.
 * Creates pentatonic scale acoustic synth chimes matching optical illusion movements.
 */
class ToneSynthesizer {

    private val sampleRate = 44100
    private var isMuted = false

    // Pentatonic scale frequencies in Hz (C4, D4, E4, G4, A4, C5, D5, E5)
    private val scaleFrequencies = floatArrayOf(
        261.63f, // C4
        293.66f, // D4
        329.63f, // E4
        392.00f, // G4
        440.00f, // A4
        523.25f, // C5
        587.33f, // D5
        659.25f  // E5
    )

    fun toggleMute(): Boolean {
        isMuted = !isMuted
        return isMuted
    }

    fun isMuted(): Boolean = isMuted

    fun playTapTone(tapCount: Int, toneMultiplier: Float = 1.0f) {
        if (isMuted) return

        CoroutineScope(Dispatchers.Default).launch {
            try {
                val index = (tapCount % scaleFrequencies.size)
                val freq = scaleFrequencies[index] * toneMultiplier
                val durationMs = 180
                val numSamples = durationMs * sampleRate / 1000
                val buffer = ShortArray(numSamples)

                for (i in 0 until numSamples) {
                    val t = i.toDouble() / sampleRate
                    // Sine wave with exponential decay envelope for smooth synth bell sound
                    val envelope = Math.exp(-6.0 * t / (durationMs / 1000.0))
                    val wave = sin(2.0 * Math.PI * freq * t) + 0.3 * sin(4.0 * Math.PI * freq * t)
                    val sample = (wave * envelope * Short.MAX_VALUE * 0.4).toInt()
                    buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }

                val track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(buffer.size * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                track.write(buffer, 0, buffer.size)
                track.play()

                // Release track resources after playback finishes
                Thread.sleep(durationMs.toLong() + 50)
                track.stop()
                track.release()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun playGameOverTone() {
        if (isMuted) return
        CoroutineScope(Dispatchers.Default).launch {
            val freqs = floatArrayOf(392.00f, 329.63f, 261.63f, 196.00f)
            for (freq in freqs) {
                try {
                    val durationMs = 120
                    val numSamples = durationMs * sampleRate / 1000
                    val buffer = ShortArray(numSamples)
                    for (i in 0 until numSamples) {
                        val t = i.toDouble() / sampleRate
                        val envelope = Math.exp(-4.0 * t / (durationMs / 1000.0))
                        val wave = sin(2.0 * Math.PI * freq * t)
                        buffer[i] = (wave * envelope * Short.MAX_VALUE * 0.35).toInt().toShort()
                    }
                    val track = AudioTrack.Builder()
                        .setAudioAttributes(
                            AudioAttributes.Builder()
                                .setUsage(AudioAttributes.USAGE_GAME)
                                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                                .build()
                        )
                        .setAudioFormat(
                            AudioFormat.Builder()
                                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                                .setSampleRate(sampleRate)
                                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                                .build()
                        )
                        .setBufferSizeInBytes(buffer.size * 2)
                        .setTransferMode(AudioTrack.MODE_STATIC)
                        .build()

                    track.write(buffer, 0, buffer.size)
                    track.play()
                    Thread.sleep(durationMs.toLong())
                    track.stop()
                    track.release()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}
