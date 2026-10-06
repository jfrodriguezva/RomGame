package com.miambiente.app.ui.screens

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * `Dispatchers.IO` no existe en el navegador (no hay hilos de E/S). La
 * pizarra lo usa para guardar y compartir; en la web basta Default.
 */
internal val Dispatchers.IO: CoroutineDispatcher get() = Dispatchers.Default
