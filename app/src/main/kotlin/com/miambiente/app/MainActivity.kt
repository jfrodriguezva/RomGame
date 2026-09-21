package com.miambiente.app

import android.os.Bundle
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.platform.LocalContext
import com.miambiente.app.game.MemoriaGdxActivity
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.miambiente.app.data.LocalServices
import com.miambiente.app.data.Services
import com.miambiente.app.theme.MiAmbienteTheme
import com.miambiente.app.ui.screens.AbecedarioScreen
import com.miambiente.app.ui.screens.AnimalesScreen
import com.miambiente.app.ui.screens.BanderasScreen
import com.miambiente.app.ui.screens.BarrasNumericasScreen
import com.miambiente.app.ui.screens.AdivinaQuienScreen
import com.miambiente.app.ui.screens.AlfabetoMovilScreen
import com.miambiente.app.ui.screens.AranaScreen
import com.miambiente.app.ui.screens.BancoDoradoScreen
import com.miambiente.app.ui.screens.BinomioScreen
import com.miambiente.app.ui.screens.BingoScreen
import com.miambiente.app.ui.screens.LoteriaScreen
import com.miambiente.app.ui.screens.BurbujasScreen
import com.miambiente.app.ui.screens.CanastaScreen
import com.miambiente.app.ui.screens.CaraScreen
import com.miambiente.app.ui.screens.CarrerasScreen
import com.miambiente.app.ui.screens.CicloAguaScreen
import com.miambiente.app.ui.screens.CicloVidaScreen
import com.miambiente.app.ui.screens.CilindrosScreen
import com.miambiente.app.ui.screens.CollageScreen
import com.miambiente.app.ui.screens.ColorearScreen
import com.miambiente.app.ui.screens.ColoresScreen
import com.miambiente.app.ui.screens.Conecta4Screen
import com.miambiente.app.ui.screens.ContarScreen
import com.miambiente.app.ui.screens.ContinentesScreen
import com.miambiente.app.ui.screens.CuerpoScreen
import com.miambiente.app.ui.screens.DadoScreen
import com.miambiente.app.ui.screens.AjedrezScreen
import com.miambiente.app.ui.screens.AjustesScreen
import com.miambiente.app.ui.screens.ArkanoidScreen
import com.miambiente.app.ui.screens.MosaicoScreen
import com.miambiente.app.ui.screens.VaquerosScreen
import com.miambiente.app.ui.screens.ComepuntosScreen
import com.miambiente.app.ui.screens.NieveScreen
import com.miambiente.app.ui.screens.EscuadronEstelarScreen
import com.miambiente.app.ui.screens.GranPremioScreen
import com.miambiente.app.ui.screens.DamasChinasScreen
import com.miambiente.app.ui.screens.DamasScreen
import com.miambiente.app.ui.screens.SnakeScreen
import com.miambiente.app.ui.screens.TetrisScreen
import com.miambiente.app.ui.screens.TopoScreen
import com.miambiente.app.ui.screens.SolitarioScreen
import com.miambiente.app.ui.screens.SolitarioAranaScreen
import com.miambiente.app.ui.screens.DiaNocheScreen
import com.miambiente.app.ui.screens.DominoScreen
import com.miambiente.app.ui.screens.DiasSemanaScreen
import com.miambiente.app.ui.screens.DietaAnimalScreen
import com.miambiente.app.ui.screens.DiferenciasScreen
import com.miambiente.app.ui.screens.ElLaScreen
import com.miambiente.app.ui.screens.EmocionesScreen
import com.miambiente.app.ui.screens.EscaleraMarronScreen
import com.miambiente.app.ui.screens.EstacionesScreen
import com.miambiente.app.ui.screens.EstadosAguaScreen
import com.miambiente.app.ui.screens.FormasScreen
import com.miambiente.app.ui.screens.FrutaVerduraScreen
import com.miambiente.app.ui.screens.GatoScreen
import com.miambiente.app.ui.screens.GloboScreen
import com.miambiente.app.ui.screens.HabitatScreen
import com.miambiente.app.ui.screens.HomeScreen
import com.miambiente.app.ui.screens.FamiliaScreen
import com.miambiente.app.ui.screens.HusosScreen
import com.miambiente.app.ui.screens.InglesScreen
import com.miambiente.app.ui.screens.InstrumentosScreen
import com.miambiente.app.ui.screens.LaberintoScreen
import com.miambiente.app.ui.screens.LadosScreen
import com.miambiente.app.ui.screens.LavadoManosScreen
import com.miambiente.app.ui.screens.LetrasLijaScreen
import com.miambiente.app.ui.screens.MayusculasScreen
import com.miambiente.app.ui.screens.MemoramaScreen
import com.miambiente.app.ui.screens.MemoriaTurnosScreen
import com.miambiente.app.ui.screens.MesaScreen
import com.miambiente.app.ui.screens.MesaSilencioScreen
import com.miambiente.app.ui.screens.MitadesScreen
import com.miambiente.app.ui.screens.NumerosScreen
import com.miambiente.app.ui.screens.OcaScreen
import com.miambiente.app.ui.screens.ObjetosScreen
import com.miambiente.app.ui.screens.OficiosScreen
import com.miambiente.app.ui.screens.OlfatoScreen
import com.miambiente.app.ui.screens.OrificiosScreen
import com.miambiente.app.ui.screens.ParesImparesScreen
import com.miambiente.app.ui.screens.PartesPlantaScreen
import com.miambiente.app.ui.screens.PatronScreen
import com.miambiente.app.ui.screens.PesoScreen
import com.miambiente.app.ui.screens.PinzaScreen
import com.miambiente.app.ui.screens.PizarraScreen
import com.miambiente.app.ui.screens.QueFaltaScreen
import com.miambiente.app.ui.screens.ReflejoColorScreen
import com.miambiente.app.ui.screens.RelojScreen
import com.miambiente.app.ui.screens.RimasScreen
import com.miambiente.app.ui.screens.RompecabezasScreen
import com.miambiente.app.ui.screens.RpsScreen
import com.miambiente.app.ui.screens.RutinaScreen
import com.miambiente.app.ui.screens.SaborScreen
import com.miambiente.app.ui.screens.SentidosScreen
import com.miambiente.app.ui.screens.SeresVivosScreen
import com.miambiente.app.ui.screens.SerpientesScreen
import com.miambiente.app.ui.screens.SilabasScreen
import com.miambiente.app.ui.screens.SingularPluralScreen
import com.miambiente.app.ui.screens.SistemaSolarScreen
import com.miambiente.app.ui.screens.SolidosScreen
import com.miambiente.app.ui.screens.SombrasScreen
import com.miambiente.app.ui.screens.SonidosInicialesScreen
import com.miambiente.app.ui.screens.TablaCienScreen
import com.miambiente.app.ui.screens.TamanosScreen
import com.miambiente.app.ui.screens.TemperaturaScreen
import com.miambiente.app.ui.screens.TexturaScreen
import com.miambiente.app.ui.screens.TierraAguaScreen
import com.miambiente.app.ui.screens.TiempoScreen
import com.miambiente.app.ui.screens.TorreRosaScreen
import com.miambiente.app.ui.screens.TransporteScreen
import com.miambiente.app.ui.screens.TrazosScreen
import com.miambiente.app.ui.screens.VibraAdivinaScreen
import com.miambiente.app.ui.screens.VidaPracticaScreen
import com.miambiente.app.ui.screens.VocalesScreen
import com.miambiente.app.ui.screens.XilofonoScreen

/**
 * Rutas de navegación — equivalente nativo del App Router de Next.js
 * (app/page.tsx, app/games/<slug>/page.tsx). Cada ruta usa el mismo id
 * que su GameDef en model/GameDef.kt, así HomeScreen navega con
 * `navController.navigate(juego.id)` sin necesitar un mapeo aparte.
 */
object Ruta {
    const val INICIO = "inicio"
    const val AJUSTES = "ajustes"
    const val FAMILIA = "familia/{id}"
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val services = Services(applicationContext)

        setContent {
            MiAmbienteTheme {
                CompositionLocalProvider(LocalServices provides services) {
                    // `null` = todavía no llegó la primera lectura de DataStore
                    // (es async, a diferencia de zustand+localStorage que es
                    // síncrono). No se crea el NavHost hasta tener el valor
                    // real: el `startDestination` de NavHost se fija una sola
                    // vez al crearse, así que decidirlo con un valor por
                    // defecto y "corregirlo después" no funciona — mandaría
                    // siempre al selector de edad aunque ya hubiera una
                    // guardada, sin forma de navegar sola a inicio.
                    val settings by produceState<com.miambiente.app.data.Settings?>(initialValue = null, services) {
                        services.settings.settings.collect { value = it }
                    }
                    val actual = settings ?: return@CompositionLocalProvider

                    val navController = rememberNavController()
                    val volver: () -> Unit = { navController.popBackStack() }

                    NavHost(
                        navController = navController,
                        startDestination = Ruta.INICIO,
                    ) {
                        composable(Ruta.INICIO) {
                            HomeScreen(
                                onAbrirFamilia = { id -> navController.navigate("familia/$id") },
                                onAjustes = { navController.navigate(Ruta.AJUSTES) },
                                nombre = actual.nombre,
                            )
                        }
                        composable(Ruta.FAMILIA) { entry ->
                            val context = LocalContext.current
                            FamiliaScreen(
                                id = entry.arguments?.getString("id").orEmpty(),
                                onAbrirJuego = { id -> navController.navigate(id) },
                                onAbrirMotor = { id ->
                                    if (id == "memoria-observacion") context.startActivity(Intent(context, MemoriaGdxActivity::class.java))
                                },
                                onVolver = volver,
                            )
                        }
                        composable(Ruta.AJUSTES) { AjustesScreen(volver) }

                        // --- MaterialQuiz ---
                        composable("formas") { FormasScreen(volver) }
                        composable("cuerpo") { CuerpoScreen(volver) }
                        composable("colores") { ColoresScreen(volver) }
                        composable("instrumentos") { InstrumentosScreen(volver) }
                        composable("oficios") { OficiosScreen(volver) }

                        // --- MaterialOrdenar ---
                        composable("torre-rosa") { TorreRosaScreen(volver) }
                        composable("dias-semana") { DiasSemanaScreen(volver) }
                        composable("estaciones") { EstacionesScreen(volver) }
                        composable("ciclo-vida") { CicloVidaScreen(volver) }

                        // --- MaterialClasificar ---
                        composable("seres-vivos") { SeresVivosScreen(volver) }
                        composable("habitat") { HabitatScreen(volver) }
                        composable("dieta-animal") { DietaAnimalScreen(volver) }
                        composable("fruta-verdura") { FrutaVerduraScreen(volver) }
                        composable("el-la") { ElLaScreen(volver) }
                        composable("pares-impares") { ParesImparesScreen(volver) }
                        composable("tamanos") { TamanosScreen(volver) }
                        composable("estados-agua") { EstadosAguaScreen(volver) }
                        composable("dia-noche") { DiaNocheScreen(volver) }
                        composable("singular-plural") { SingularPluralScreen(volver) }
                        composable("transporte") { TransporteScreen(volver) }

                        // --- MaterialTransferir ---
                        composable("pinza") { PinzaScreen(volver) }
                        composable("husos") { HusosScreen(volver) }
                        composable("contar") { ContarScreen(volver) }

                        // --- MaterialQuiz: lote 2 ---
                        composable("animales") { AnimalesScreen(volver) }
                        composable("solidos") { SolidosScreen(volver) }
                        composable("sentidos") { SentidosScreen(volver) }
                        composable("partes-planta") { PartesPlantaScreen(volver) }
                        composable("banderas") { BanderasScreen(volver) }
                        composable("sonidos-iniciales") { SonidosInicialesScreen(volver) }
                        composable("vocales") { VocalesScreen(volver) }
                        composable("abecedario") { AbecedarioScreen(volver) }
                        composable("ingles") { InglesScreen(volver) }
                        composable("emociones") { EmocionesScreen(volver) }
                        composable("tiempo") { TiempoScreen(volver) }
                        composable("reloj") { RelojScreen(volver) }
                        composable("sombras") { SombrasScreen(volver) }
                        composable("letras-lija") { LetrasLijaScreen(volver) }

                        // --- MaterialOrdenar: lote 2 ---
                        composable("escalera-marron") { EscaleraMarronScreen(volver) }
                        composable("cilindros") { CilindrosScreen(volver) }
                        composable("sistema-solar") { SistemaSolarScreen(volver) }
                        composable("ciclo-agua") { CicloAguaScreen(volver) }
                        composable("rutina") { RutinaScreen(volver) }
                        composable("mesa") { MesaScreen(volver) }
                        composable("lavado-manos") { LavadoManosScreen(volver) }
                        composable("barras-numericas") { BarrasNumericasScreen(volver) }
                        composable("numeros") { NumerosScreen(volver) }

                        // --- MaterialClasificar: lote 2 ---
                        composable("textura") { TexturaScreen(volver) }
                        composable("temperatura") { TemperaturaScreen(volver) }
                        composable("peso") { PesoScreen(volver) }
                        composable("sabor") { SaborScreen(volver) }
                        composable("olfato") { OlfatoScreen(volver) }
                        composable("mayusculas") { MayusculasScreen(volver) }
                        composable("lados") { LadosScreen(volver) }
                        composable("mitades") { MitadesScreen(volver) }
                        composable("rimas") { RimasScreen(volver) }
                        composable("silabas") { SilabasScreen(volver) }
                        composable("continentes") { ContinentesScreen(volver) }
                        composable("tierra-agua") { TierraAguaScreen(volver) }
                        composable("orificios") { OrificiosScreen(volver) }

                        // --- MaterialOrdenar: lote 3 ---
                        composable("vida-practica") { VidaPracticaScreen(volver) }

                        // --- Independientes ---
                        composable("gato") { GatoScreen(volver) }
                        composable("rps") { RpsScreen(volver) }
                        composable("memorama") { MemoramaScreen(volver) }
                        composable("que-falta") { QueFaltaScreen(volver) }
                        composable("diferencias") { DiferenciasScreen(volver) }
                        composable("objetos") { ObjetosScreen(volver) }
                        composable("memoria-turnos") { MemoriaTurnosScreen(volver) }
                        composable("oca") { OcaScreen(volver) }
                        composable("serpientes") { SerpientesScreen(volver) }
                        composable("dado") { DadoScreen(volver) }
                        composable("patron") { PatronScreen(volver) }
                        composable("pizarra") { PizarraScreen(volver) }
                        composable("collage") { CollageScreen(volver) }
                        composable("colorear") { ColorearScreen(volver) }
                        composable("xilofono") { XilofonoScreen(volver) }
                        composable("mesa-silencio") { MesaSilencioScreen(volver) }
                        composable("cara") { CaraScreen(volver) }
                        composable("alfabeto-movil") { AlfabetoMovilScreen(volver) }
                        composable("trazos") { TrazosScreen(volver) }
                        composable("rompecabezas") { RompecabezasScreen(volver) }
                        composable("binomio") { BinomioScreen(volver) }
                        composable("tabla-cien") { TablaCienScreen(volver) }
                        composable("banco-dorado") { BancoDoradoScreen(volver) }
                        composable("bingo") { BingoScreen(volver) }
                        composable("loteria") { LoteriaScreen(volver) }
                        composable("domino") { DominoScreen(volver) }
                        composable("conecta4") { Conecta4Screen(volver) }
                        composable("adivinaquien") { AdivinaQuienScreen(volver) }
                        composable("damas") { DamasScreen(volver) }
                        composable("damas-chinas") { DamasChinasScreen(volver) }
                        composable("tetris") { TetrisScreen(volver) }
                        composable("snake") { SnakeScreen(volver) }
                        composable("arkanoid") { ArkanoidScreen(volver) }
                        composable("topo") { TopoScreen(volver) }
                        composable("mosaico") { MosaicoScreen(volver) }
                        composable("vaqueros") { VaquerosScreen(volver) }
                        composable("comepuntos") { ComepuntosScreen(volver) }
                        composable("nieve") { NieveScreen(volver) }
                        composable("escuadron-estelar") { EscuadronEstelarScreen(volver) }
                        composable("gran-premio") { GranPremioScreen(volver) }
                        composable("solitario") { SolitarioScreen(volver) }
                        composable("arana-cartas") { SolitarioAranaScreen(volver) }
                        composable("ajedrez") { AjedrezScreen(volver) }

                        // --- Movimiento y coordinación ---
                        composable("laberinto") { LaberintoScreen(volver) }
                        composable("burbujas") { BurbujasScreen(volver) }
                        composable("canasta") { CanastaScreen(volver) }
                        composable("globo") { GloboScreen(volver) }
                        composable("arana") { AranaScreen(volver) }
                        composable("carreras") { CarrerasScreen(volver) }

                        // --- Exclusivos de la versión nativa ---
                        composable("vibra-adivina") { VibraAdivinaScreen(volver) }
                        composable("reflejo-color") { ReflejoColorScreen(volver) }
                    }
                }
            }
        }
    }
}
