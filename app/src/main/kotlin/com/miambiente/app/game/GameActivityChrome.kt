package com.miambiente.app.game

import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import android.window.OnBackInvokedDispatcher

/** Navegacion comun y siempre accesible sobre LibGDX y Godot. */
internal fun Activity.installGameChrome() {
    val density = resources.displayMetrics.density
    fun dp(value: Int) = (value * density).toInt()
    val button = TextView(this).apply {
        text = "‹"
        textSize = 34f
        setTextColor(Color.WHITE)
        typeface = Typeface.DEFAULT_BOLD
        gravity = Gravity.CENTER
        contentDescription = "Volver a materiales"
        elevation = dp(8).toFloat()
        background = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(Color.rgb(72, 61, 139))
            setStroke(dp(2), Color.argb(150, 255, 255, 255))
        }
        setOnClickListener { finish() }
        systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    }
    addContentView(
        button,
        FrameLayout.LayoutParams(dp(52), dp(52), Gravity.TOP or Gravity.START).apply {
            leftMargin = dp(12)
            topMargin = dp(12)
        },
    )
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        onBackInvokedDispatcher.registerOnBackInvokedCallback(
            OnBackInvokedDispatcher.PRIORITY_DEFAULT,
        ) { finish() }
    }
}
