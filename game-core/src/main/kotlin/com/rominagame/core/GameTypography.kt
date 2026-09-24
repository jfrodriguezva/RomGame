package com.rominagame.core

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator

/** Tipografia nativa de alta resolucion para todas las experiencias LibGDX. */
internal object GameTypography {
    private const val CHARACTERS =
        "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789" +
            " ÁÉÍÓÚÜÑáéíóúüñ¿?¡!.,:;·-/+()[]%#"

    fun create(size: Int): BitmapFont {
        val systemFont = Gdx.files.absolute("/system/fonts/Roboto-Regular.ttf")
        if (!systemFont.exists()) return BitmapFont().apply { data.setScale(size / 16f) }
        val generator = FreeTypeFontGenerator(systemFont)
        return try {
            generator.generateFont(FreeTypeFontGenerator.FreeTypeFontParameter().apply {
                this.size = size
                characters = CHARACTERS
                minFilter = Texture.TextureFilter.Linear
                magFilter = Texture.TextureFilter.Linear
                borderWidth = 0.35f
            })
        } finally {
            generator.dispose()
        }
    }
}
