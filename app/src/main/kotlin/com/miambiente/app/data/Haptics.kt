package com.miambiente.app.data

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

/** Equivalente nativo de lib/haptics.ts (navigator.vibrate). */
enum class Patron { TOQUE, ACIERTO, ERROR, LOGRO }

private fun patronMs(p: Patron): LongArray = when (p) {
    Patron.TOQUE -> longArrayOf(0, 10)
    Patron.ACIERTO -> longArrayOf(0, 12, 40, 18)
    Patron.ERROR -> longArrayOf(0, 28)
    Patron.LOGRO -> longArrayOf(0, 18, 60, 18, 60, 30)
}

class Haptics(context: Context) {
    /** Gatea desde Ajustes ("Vibración"); `Services` lo mantiene al día. */
    @Volatile var activo: Boolean = true

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        manager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    fun vibrar(patron: Patron) {
        if (!activo) return
        val v = vibrator ?: return
        if (!v.hasVibrator()) return
        val timings = patronMs(patron)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createWaveform(timings, -1))
        } else {
            @Suppress("DEPRECATION")
            v.vibrate(timings, -1)
        }
    }
}
