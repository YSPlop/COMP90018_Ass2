package com.example.safecircle.sensors

import android.Manifest
import android.util.Log
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.core.app.ActivityCompat
import kotlin.math.abs
import kotlinx.coroutines.*

class NoiseSensorManager(
    private val context: Context,
    private val callback: (Boolean, Double) -> Unit
){
    private var job: Job? = null
    private var recorder: AudioRecord? = null

    fun init(){
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.w("test", "Noise sensor service will not start because audio record permission not granted.")
            return
        }

        start();
        job = CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                val amplitude = getAmplitude()
                callback(true, amplitude)
                delay(100)
            }
        }
    }

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