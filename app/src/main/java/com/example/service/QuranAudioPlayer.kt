package com.example.service

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.data.repository.MosqueRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

data class QuranAudioState(
    val isPlaying: Boolean = false,
    val isLoading: Boolean = false,
    val currentSurahNumber: Int = 1,
    val currentPositionMs: Int = 0,
    val durationMs: Int = 0,
    val playbackSpeed: Float = 1.0f,
    val selectedQariId: String = "mishary",
    val errorMessage: String? = null
)

object QuranAudioPlayer {
    private const val TAG = "QuranAudioPlayer"
    private var mediaPlayer: MediaPlayer? = null
    private val handler = Handler(Looper.getMainLooper())

    private val _audioState = MutableStateFlow(QuranAudioState())
    val audioState: StateFlow<QuranAudioState> = _audioState.asStateFlow()

    private val progressRunnable = object : Runnable {
        override fun run() {
            mediaPlayer?.let { player ->
                if (player.isPlaying) {
                    _audioState.value = _audioState.value.copy(
                        currentPositionMs = player.currentPosition,
                        durationMs = player.duration
                    )
                    handler.postDelayed(this, 500)
                }
            }
        }
    }

    fun playSurah(context: Context, surahNumber: Int, qariId: String = _audioState.value.selectedQariId) {
        val qari = MosqueRepository.qariList.find { it.id == qariId } ?: MosqueRepository.qariList.first()
        val url = String.format(Locale.US, qari.serverUrlPattern, surahNumber)

        _audioState.value = _audioState.value.copy(
            isLoading = true,
            currentSurahNumber = surahNumber,
            selectedQariId = qariId,
            errorMessage = null
        )

        try {
            stop()
            val player = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(url)
                setOnPreparedListener { mp ->
                    _audioState.value = _audioState.value.copy(
                        isLoading = false,
                        isPlaying = true,
                        durationMs = mp.duration,
                        currentPositionMs = 0
                    )
                    applySpeed(_audioState.value.playbackSpeed)
                    mp.start()
                    handler.post(progressRunnable)
                }
                setOnCompletionListener {
                    _audioState.value = _audioState.value.copy(
                        isPlaying = false,
                        currentPositionMs = 0
                    )
                    handler.removeCallbacks(progressRunnable)
                }
                setOnErrorListener { _, what, extra ->
                    Log.w(TAG, "Audio play error: what=$what, extra=$extra")
                    _audioState.value = _audioState.value.copy(
                        isLoading = false,
                        isPlaying = false,
                        errorMessage = "তেলাওয়াত অডিও লোড করা সম্ভব হয়নি। ইন্টারনেট সংযোগ চেক করুন।"
                    )
                    true
                }
                prepareAsync()
            }
            mediaPlayer = player
        } catch (e: Exception) {
            Log.e(TAG, "Exception initializing MediaPlayer", e)
            _audioState.value = _audioState.value.copy(
                isLoading = false,
                isPlaying = false,
                errorMessage = "অডিও প্লেয়ার চালু করা যায়নি。"
            )
        }
    }

    fun togglePlayPause() {
        mediaPlayer?.let { player ->
            if (player.isPlaying) {
                player.pause()
                _audioState.value = _audioState.value.copy(isPlaying = false)
                handler.removeCallbacks(progressRunnable)
            } else {
                player.start()
                _audioState.value = _audioState.value.copy(isPlaying = true)
                handler.post(progressRunnable)
            }
        }
    }

    fun seekTo(positionMs: Int) {
        mediaPlayer?.let { player ->
            player.seekTo(positionMs)
            _audioState.value = _audioState.value.copy(currentPositionMs = positionMs)
        }
    }

    fun seekRelative(offsetMs: Int) {
        mediaPlayer?.let { player ->
            val target = (player.currentPosition + offsetMs).coerceIn(0, player.duration)
            seekTo(target)
        }
    }

    fun setSpeed(speed: Float) {
        _audioState.value = _audioState.value.copy(playbackSpeed = speed)
        applySpeed(speed)
    }

    private fun applySpeed(speed: Float) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mediaPlayer?.let { player ->
                try {
                    player.playbackParams = player.playbackParams.setSpeed(speed)
                } catch (e: Exception) {
                    Log.w(TAG, "Could not set playback speed", e)
                }
            }
        }
    }

    fun stop() {
        handler.removeCallbacks(progressRunnable)
        mediaPlayer?.let { player ->
            try {
                if (player.isPlaying) {
                    player.stop()
                }
                player.release()
            } catch (e: Exception) {
                Log.w(TAG, "Error releasing MediaPlayer", e)
            }
        }
        mediaPlayer = null
        _audioState.value = _audioState.value.copy(isPlaying = false, isLoading = false)
    }
}
