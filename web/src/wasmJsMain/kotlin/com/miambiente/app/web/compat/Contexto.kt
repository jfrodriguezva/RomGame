package android.content

// Compatibilidad web: lo mínimo de Context/Intent que usa la pizarra para
// compartir un dibujo. En el navegador "compartir" descarga la imagen.

open class Context {
    fun startActivity(intent: Intent) {
        val elegido = intent.extras["android.intent.extra.INTENT"] as? Intent ?: intent
        (elegido.extras[Intent.EXTRA_STREAM] as? com.miambiente.app.data.ArchivoWeb)?.let {
            com.miambiente.app.data.descargarArchivo(it)
        }
    }
}

class Intent(val action: String? = null) {
    var type: String? = null
    internal val extras = mutableMapOf<String, Any?>()

    fun putExtra(clave: String, valor: Any?): Intent { extras[clave] = valor; return this }
    fun addFlags(flags: Int): Intent = this

    companion object {
        const val ACTION_SEND = "android.intent.action.SEND"
        const val EXTRA_STREAM = "android.intent.extra.STREAM"
        const val FLAG_GRANT_READ_URI_PERMISSION = 1

        fun createChooser(objetivo: Intent, titulo: CharSequence?): Intent =
            Intent("android.intent.action.CHOOSER").putExtra("android.intent.extra.INTENT", objetivo)
    }
}
