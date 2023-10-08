package com.example.safecircle.sensors

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import kotlin.math.abs
import kotlinx.coroutines.*

/**
 * Detects ambient noise amplitude at a set interval.
 */
class NoiseSensorManager(
    private val context: Context,
    private val callback: (Double) -> Unit
){
    private var job: Job? = null
    private var recorder: AudioRecord? = null

    /**
     * Start detecting sound.
     */
    fun init(){

        start();
        job = CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                val amplitude = getAmplitude()
                callback(amplitude)
                delay(100)
            }
        }
    }

    /**
     * Stop detecting sound.
     */
    fun end(){
        stop();
        job?.cancel();
    }

    @SuppressLint("MissingPermission")
    private fun start(){
        if (recorder == null) {
            val minBufferSize = AudioRecord.getMinBufferSize(
                8000,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            recorder = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                8000,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                minBufferSize
            )
            recorder?.startRecording()
        }
    }

    private fun stop(){
        recorder?.stop()
        recorder?.release()
        recorder = null
    }

    /**
     * Returns the amplitude of the most recently detected noise.
     */
    fun getAmplitude(): Double{
        if (recorder != null) {
            val buffer = ShortArray(100)
            recorder?.read(buffer, 0, 100)
            var max = 0
            for (s in buffer) {
                if (abs(s.toInt()) > max) {
                    max = abs(s.toInt())
                }
            }
            return max.toDouble()
        }
        return -1.0
    }
}