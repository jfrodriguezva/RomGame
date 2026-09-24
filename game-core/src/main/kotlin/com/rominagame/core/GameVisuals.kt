package com.rominagame.core

import com.badlogic.gdx.graphics.glutils.ShapeRenderer

/** Rectangulo redondeado consistente para tarjetas, botones y tableros. */
internal fun ShapeRenderer.roundedRect(x: Float, y: Float, width: Float, height: Float, requestedRadius: Float = 14f) {
    val radius = requestedRadius.coerceAtMost(width / 2f).coerceAtMost(height / 2f)
    rect(x + radius, y, width - radius * 2f, height)
    rect(x, y + radius, width, height - radius * 2f)
    circle(x + radius, y + radius, radius, 20)
    circle(x + width - radius, y + radius, radius, 20)
    circle(x + radius, y + height - radius, radius, 20)
    circle(x + width - radius, y + height - radius, radius, 20)
}
