@file:OptIn(ExperimentalWasmJsInterop::class)

package com.miambiente.app.data

/**
 * Puente mínimo con el navegador. Cada función envuelve su llamada en
 * try/catch: en modo privado, sin audio o sin voz, la app sigue funcionando
 * en silencio en vez de detenerse.
 */

// ---- localStorage (el equivalente de DataStore) ----
internal fun almacenLeer(clave: String): String? =
    js("(() => { try { return window.localStorage.getItem(clave); } catch (e) { return null; } })()")

internal fun almacenEscribir(clave: String, valor: String): Unit =
    js("(() => { try { window.localStorage.setItem(clave, valor); } catch (e) {} })()")

internal fun almacenCantidad(): Int =
    js("(() => { try { return window.localStorage.length; } catch (e) { return 0; } })()")

internal fun almacenClave(indice: Int): String? =
    js("(() => { try { return window.localStorage.key(indice); } catch (e) { return null; } })()")

internal fun almacenBorrar(clave: String): Unit =
    js("(() => { try { window.localStorage.removeItem(clave); } catch (e) {} })()")

/** Descarga un archivo (base64) con el nombre dado: "compartir" en el navegador. */
internal fun descargarNavegador(base64: String, nombre: String, tipo: String): Unit = js(
    """(() => { try {
        const a = document.createElement('a');
        a.href = 'data:' + tipo + ';base64,' + base64;
        a.download = nombre.endsWith('.png') || nombre.endsWith('.jpg') ? nombre : nombre + '.jpg';
        document.body.appendChild(a); a.click(); a.remove();
    } catch (e) {} })()"""
)

internal fun ahoraMsEpoch(): Long = fechaMs().toLong()

private fun fechaMs(): Double = js("Date.now()")

// ---- Fecha local, en días desde la época ----
internal fun diaLocalNavegador(): Double =
    js("(() => { const d = new Date(); return Math.floor((d.getTime() - d.getTimezoneOffset() * 60000) / 86400000); })()")

// ---- Reloj de alta resolución, en milisegundos ----
internal fun ahoraMs(): Double = js("performance.now()")

// ---- WebAudio: un tono corto con envolvente ----
internal fun tonoNavegador(frecuencia: Double, duracionMs: Int, tipo: String, volumen: Double, retrasoMs: Int): Unit = js(
    """(() => { try {
        const w = window;
        w.__romCtx = w.__romCtx || new (w.AudioContext || w.webkitAudioContext)();
        const c = w.__romCtx;
        if (c.state === 'suspended') c.resume();
        const t0 = c.currentTime + retrasoMs / 1000;
        const fin = t0 + duracionMs / 1000;
        const o = c.createOscillator();
        const g = c.createGain();
        o.type = tipo;
        o.frequency.value = frecuencia;
        g.gain.setValueAtTime(0, t0);
        g.gain.linearRampToValueAtTime(volumen, t0 + 0.012);
        g.gain.exponentialRampToValueAtTime(0.0001, fin);
        o.connect(g); g.connect(c.destination);
        o.start(t0); o.stop(fin + 0.05);
    } catch (e) {} })()"""
)

// ---- Fondo musical: un acorde suave en bucle ----
internal fun acordeNavegador(frecuencias: String, volumen: Double): Unit = js(
    """(() => { try {
        const w = window;
        w.__romCtx = w.__romCtx || new (w.AudioContext || w.webkitAudioContext)();
        const c = w.__romCtx;
        if (c.state === 'suspended') c.resume();
        if (w.__romPad) { w.__romPad.forEach(n => { try { n.stop(); } catch (e) {} }); }
        const g = c.createGain();
        g.gain.setValueAtTime(0, c.currentTime);
        g.gain.linearRampToValueAtTime(volumen, c.currentTime + 1.5);
        g.connect(c.destination);
        w.__romPadGain = g;
        w.__romPad = frecuencias.split(',').map((f, i) => {
            const o = c.createOscillator();
            const lfo = c.createOscillator();
            const lg = c.createGain();
            o.type = 'sine';
            o.frequency.value = parseFloat(f);
            lfo.frequency.value = 0.12 + i * 0.03;
            lg.gain.value = volumen * 0.4;
            lfo.connect(lg); lg.connect(g.gain);
            o.connect(g); o.start(); lfo.start();
            return o;
        });
    } catch (e) {} })()"""
)

internal fun silenciarAcordeNavegador(): Unit = js(
    """(() => { try {
        const w = window;
        if (w.__romPad) { w.__romPad.forEach(n => { try { n.stop(); } catch (e) {} }); w.__romPad = null; }
    } catch (e) {} })()"""
)

// ---- Voz (Web Speech API) ----
internal fun vozDisponible(): Boolean = js("typeof window.speechSynthesis !== 'undefined'")

internal fun hablarNavegador(texto: String, velocidad: Double): Unit = js(
    """(() => { try {
        const s = window.speechSynthesis;
        s.cancel();
        const u = new SpeechSynthesisUtterance(texto);
        u.lang = 'es-MX';
        u.rate = velocidad;
        u.pitch = 1.08;
        const voces = s.getVoices().filter(v => v.lang && v.lang.toLowerCase().startsWith('es'));
        const v = voces.find(v => v.lang === 'es-MX') || voces.find(v => v.lang === 'es-US') || voces[0];
        if (v) u.voice = v;
        s.speak(u);
    } catch (e) {} })()"""
)

internal fun callarNavegador(): Unit = js("(() => { try { window.speechSynthesis.cancel(); } catch (e) {} })()")

// ---- Vibración (solo en teléfonos que la soportan) ----
internal fun vibrarNavegador(patron: String): Unit =
    js("(() => { try { if (navigator.vibrate) navigator.vibrate(patron.split(',').map(Number)); } catch (e) {} })()")
