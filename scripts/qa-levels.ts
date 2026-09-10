/**
 * QA de las curvas de niveles: valida que los 100 niveles de cada material
 * generen configuraciones sanas (rangos correctos, sin NaN, sin pedir más
 * elementos de los que hay disponibles) y que el catálogo sea consistente.
 *
 * No sustituye probarlo con las manos, pero atrapa el tipo de error que un
 * cambio en una curva puede introducir sin que ningún build lo note: un
 * nivel 100 con `objetivo: 11` en un material que solo tiene números del 0
 * al 9, por ejemplo.
 *
 *   npx tsx scripts/qa-levels.ts
 */
import { games, materialesConNiveles } from "../data/games";
import { LEVEL_COUNT } from "../lib/levels";

import { CICLO_VIDA_LEVELS } from "../data/levels/ciclo-vida";
import { SISTEMA_SOLAR_LEVELS } from "../data/levels/sistema-solar";
import { DOBLAR_LEVELS } from "../data/levels/doblar";
import { SILABAS_LEVELS, palabrasPara } from "../data/levels/silabas";
import { EL_LA_LEVELS, PALABRAS_GENERO } from "../data/levels/el-la";
import { PARES_IMPARES_LEVELS, cantidadesPara } from "../data/levels/pares-impares";
import { PINZA_LEVELS } from "../data/levels/pinza";
import { HUSOS_LEVELS } from "../data/levels/husos";

let fallas = 0;
function fallo(msg: string) {
  fallas++;
  console.error(`✗ ${msg}`);
}
function ok(msg: string) {
  console.log(`✓ ${msg}`);
}

// ---------------------------------------------------------------------------
// 1. Catálogo: slugs únicos, ids consistentes, edades sanas
// ---------------------------------------------------------------------------
const slugs = new Set<string>();
for (const g of games) {
  if (slugs.has(g.slug)) fallo(`slug duplicado: ${g.slug}`);
  slugs.add(g.slug);
  if (g.id !== g.slug) fallo(`id distinto de slug en ${g.slug} (id=${g.id})`);
  const [desde, hasta] = g.edad;
  if (desde < 2 || hasta > 7 || desde > hasta) {
    fallo(`edad rara en ${g.slug}: [${desde}, ${hasta}]`);
  }
}
ok(`${games.length} materiales, slugs únicos, ids consistentes`);
ok(`${materialesConNiveles.length} materiales con niveles, ${games.length - materialesConNiveles.length} libres`);

// ---------------------------------------------------------------------------
// 2. Forma genérica: 100 niveles, campo level secuencial, sin NaN/undefined
// ---------------------------------------------------------------------------
function validarForma<T extends { level: number }>(nombre: string, levels: T[]) {
  if (levels.length !== LEVEL_COUNT) {
    fallo(`${nombre}: tiene ${levels.length} niveles, deberían ser ${LEVEL_COUNT}`);
    return;
  }
  levels.forEach((cfg, i) => {
    if (cfg.level !== i + 1) fallo(`${nombre}: nivel en índice ${i} trae level=${cfg.level}`);
    for (const [k, v] of Object.entries(cfg as unknown as Record<string, unknown>)) {
      if (typeof v === "number" && Number.isNaN(v)) fallo(`${nombre} nivel ${cfg.level}: ${k} es NaN`);
    }
  });
}

// ---------------------------------------------------------------------------
// 3. Reglas propias de cada material nuevo de esta sesión
// ---------------------------------------------------------------------------
validarForma("ciclo-vida", CICLO_VIDA_LEVELS);
CICLO_VIDA_LEVELS.forEach((c) => {
  if (c.cantidad < 1 || c.cantidad > 4) fallo(`ciclo-vida nivel ${c.level}: cantidad fuera de 1-4 (${c.cantidad})`);
  if (!c.invertido) fallo(`ciclo-vida nivel ${c.level}: invertido debería ser true`);
});
ok("ciclo-vida: cantidad siempre 1-4, invertido siempre true");

validarForma("sistema-solar", SISTEMA_SOLAR_LEVELS);
SISTEMA_SOLAR_LEVELS.forEach((c) => {
  if (c.cantidad < 1 || c.cantidad > 8) fallo(`sistema-solar nivel ${c.level}: cantidad fuera de 1-8 (${c.cantidad})`);
});
ok("sistema-solar: cantidad siempre 1-8");

validarForma("doblar", DOBLAR_LEVELS);
DOBLAR_LEVELS.forEach((c) => {
  if (c.cantidad < 1 || c.cantidad > 4) fallo(`doblar nivel ${c.level}: cantidad fuera de 1-4 (${c.cantidad})`);
});
ok("doblar: cantidad siempre 1-4");

validarForma("silabas", SILABAS_LEVELS);
SILABAS_LEVELS.forEach((c) => {
  const pool = palabrasPara(c.criterio).length;
  if (c.cantidad > pool) fallo(`silabas nivel ${c.level}: pide ${c.cantidad} palabras pero solo hay ${pool}`);
  if (![2, 3, 4].includes(c.criterio)) fallo(`silabas nivel ${c.level}: criterio inválido (${c.criterio})`);
});
ok("silabas: cantidad nunca excede el banco de palabras disponible");

validarForma("el-la", EL_LA_LEVELS);
EL_LA_LEVELS.forEach((c) => {
  if (c.cantidad > PALABRAS_GENERO.length) {
    fallo(`el-la nivel ${c.level}: pide ${c.cantidad} palabras pero solo hay ${PALABRAS_GENERO.length}`);
  }
});
ok("el-la: cantidad nunca excede el banco de palabras");

validarForma("pares-impares", PARES_IMPARES_LEVELS);
PARES_IMPARES_LEVELS.forEach((c) => {
  const pool = cantidadesPara(c.maxNumero).length;
  if (c.cantidad > pool) fallo(`pares-impares nivel ${c.level}: pide ${c.cantidad} tarjetas pero solo hay ${pool}`);
  if (c.maxNumero < 2 || c.maxNumero > 9) fallo(`pares-impares nivel ${c.level}: maxNumero fuera de 2-9 (${c.maxNumero})`);
});
ok("pares-impares: cantidad nunca excede las cantidades disponibles hasta maxNumero");

validarForma("pinza", PINZA_LEVELS);
PINZA_LEVELS.forEach((c) => {
  if (c.objetivo < 1) fallo(`pinza nivel ${c.level}: objetivo < 1 (${c.objetivo})`);
  if (c.origen < c.objetivo) fallo(`pinza nivel ${c.level}: origen (${c.origen}) menor que objetivo (${c.objetivo})`);
});
ok("pinza: origen siempre alcanza para cubrir el objetivo");

validarForma("husos", HUSOS_LEVELS);
const POOL_HUSOS = 9;
HUSOS_LEVELS.forEach((c) => {
  if (c.objetivo < 0 || c.objetivo > 9) fallo(`husos nivel ${c.level}: objetivo fuera de 0-9 (${c.objetivo})`);
  if (c.objetivo > POOL_HUSOS) fallo(`husos nivel ${c.level}: objetivo (${c.objetivo}) excede el pool de husos (${POOL_HUSOS})`);
});
ok("husos: objetivo siempre 0-9 y nunca excede el pool de husos disponibles");

// ---------------------------------------------------------------------------
console.log("");
if (fallas > 0) {
  console.error(`${fallas} problema(s) encontrados.`);
  process.exit(1);
} else {
  console.log("Todo limpio.");
}
