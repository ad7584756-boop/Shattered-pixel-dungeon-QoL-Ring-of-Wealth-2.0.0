# Ring of Wealth QoL 1.0.4 — cambios integrados

## Base y compatibilidad

- Base de trabajo: código fuente completo de Shattered Pixel Dungeon QoL 1.0.3.
- Se integraron las modificaciones comprobables del archivo 1.0.4 recibido: versión 1.0.4-QoL (código 923), generación de varitas, conversión universal de Command y glifo Fortune.
- Se conservaron los archivos del proyecto 1.0.3 que no aparecían en el ZIP 1.0.4 recuperado.
- Se conserva sin cambios la fórmula original de suerte, la fórmula de multiplicador, los contadores persistentes y la progresión original de recompensas.

## Anillo de Riqueza

- Se conserva exactamente `wealthLevelMultiplier`: x4,0 inicial y +0,1 cada tres niveles efectivos.
- Se conserva `wealthLuckPercent`, incluida su progresión original basada en Fibonacci y parte fraccionaria.
- Se conserva el sistema de intervalos persistentes y la separación entre recompensas de consumibles y equipamiento.
- Se mantiene la categoría de varitas añadida en 1.0.4.
- Se añade generación de armas arrojadizas mediante `Generator.randomMissile(floorset, true)`.
- Las armas arrojadizas heredan la regla de nivel de `Weapon`; armas, armaduras, varitas y anillos conservan el mínimo efectivo de nivel del código 1.0.4.

## Rareza mística

Las recompensas místicas utilizan objetos reales del equipo inicial de cada héroe:

| Héroe | Objeto real utilizado |
|---|---|
| Guerrero | `BrokenSeal` |
| Mago | `MagesStaff` imbuido con `WandOfMagicMissile` |
| Pícaro | `CloakOfShadows` |
| Cazadora | `SpiritBow` |
| Duelista | `Rapier` |
| Clérigo | `HolyTome` |

La probabilidad nueva comienza en 0,5% por tirada de equipamiento y aumenta 0,1 puntos porcentuales por nivel efectivo, con un máximo de 4%.

## Rareza divina

- La recompensa divina utiliza `ScrollOfMetamorphosis`, objeto original del juego.
- Su interfaz original permite reemplazar un talento por otro talento de la misma categoría de nivel perteneciente a otra clase.
- Se reutiliza el sistema original de talentos, sus validaciones, la interfaz y la persistencia; no se crean talentos ni árboles paralelos.
- La probabilidad nueva comienza en 0,1% por tirada de equipamiento y aumenta 0,025 puntos porcentuales por nivel efectivo, con un máximo de 1%.
- Esta mecánica es un intercambio de talento, no un punto de talento adicional ni una ranura extra.

## Command universal

- Se conserva la conversión universal de 1.0.4 a Pergaminos de Mejora para pociones, pergaminos, semillas, runas, bombas, hechizos, comida y dardos con punta compatibles.
- Se añaden `Honeypot`, `ArcaneResin`, `LiquidMetal` y `Stylus`, consumibles/materiales de uso único reales que no estaban incluidos en la lista universal inicial.
- Se conservan las restricciones de objetos malditos/equipados, objetos únicos no convertibles y el desafío `NO_SCROLLS`.
- Se reutiliza el flujo existente de `choicesFor`, `limitUpgradeScrolls`, `applyChoice` y `buildReplacement`; no se crea un segundo sistema de conversión.

## Glifo Fortune

- Se conserva el glifo `Fortune` de la integración 1.0.4 y su registro en las rarezas de glifos.
- Sus recompensas siguen reutilizando `genFortuneReward`; por ello pueden incluir las categorías de equipamiento ampliadas y las rarezas superiores.

## Funciones QoL y límites de la fuente

- Se conservaron todos los archivos y funciones presentes en el código fuente 1.0.3 y las modificaciones verificables de 1.0.4.
- No se encontró en ninguno de los dos árboles de código un filtro/configuración de recogida automática por tipo de objeto. No se ha inventado ni se declara preservada una función que no aparece en las fuentes recibidas.

## Estado de verificación

- Verificación de integridad del ZIP 1.0.3: correcta.
- El ZIP 1.0.4 original tenía offsets internos dañados; se recuperaron 10.140 entradas legibles y se compararon con 1.0.3. Se restauraron desde 1.0.3 los archivos ausentes en esa copia recuperada.
- Comparación de fórmula y métodos de suerte: pendiente de la verificación automatizada final del paquete.
- Compilación Gradle/APK: no ejecutable en este entorno porque el wrapper necesita descargar Gradle 9.5.0 y el entorno no tiene acceso DNS a `services.gradle.org`; tampoco hay Android SDK instalado.
- Las pruebas estáticas no sustituyen una compilación ni pruebas dentro del juego.
