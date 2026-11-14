# Informe de verificación — Ring of Wealth QoL 1.0.4

Fecha de preparación: 2026-11-14.

## Comprobaciones completadas

- [x] ZIP base 1.0.3: integridad comprobada y extracción completa.
- [x] ZIP 1.0.4 recibido: recuperación de 10.140 entradas legibles; 99 entradas/directorios iniciales no recuperables desde ese archivo dañado. Los archivos que faltaban se conservaron desde 1.0.3.
- [x] Comparación de inventario: los 9.847 archivos de 1.0.3 siguen presentes en el proyecto final.
- [x] Comparación de fórmulas: `wealthLevelMultiplier`, `wealthLuckPercent`, `fractionalPart`, `fibonacci`, `dropChanceMultiplier`, los métodos de intentos y las tablas de consumibles comparan idénticos con 1.0.3.
- [x] Generación de armas, armaduras, varitas, anillos, armas arrojadizas y artefactos: referencias a las API existentes verificadas en el código fuente.
- [x] Recompensas místicas: las seis asignaciones se contrastaron con los objetos iniciales definidos por `HeroClass`.
- [x] Recompensa divina: utiliza `ScrollOfMetamorphosis` y su sistema original de intercambio de talentos entre clases.
- [x] Command universal: comprobación estática de pociones, pergaminos, semillas, runas, bombas, comida, hechizos, dardos con punta, Honeypot, Arcane Resin, Liquid Metal y Stylus; conversión a `ScrollOfUpgrade` y restricción `NO_SCROLLS`.
- [x] Glifo Fortune: clase y registro en `Armor.Glyph` comprobados.
- [x] Script de verificación estática: todas las aserciones pasan.
- [x] No se eliminaron archivos de la fuente 1.0.3.

## Comprobaciones no completadas

- [ ] Compilación Gradle: no pudo iniciarse. `gradlew` intentó descargar Gradle 9.5.0 desde `services.gradle.org`, pero el entorno no pudo resolver el host (`UnknownHostException`).
- [ ] Compilación Android/APK: no disponible; no hay Android SDK configurado en el entorno.
- [ ] Pruebas de ejecución dentro de Shattered Pixel Dungeon: no realizadas.
- [ ] Pruebas de guardado/carga y persistencia de recompensas: no realizadas en una instancia ejecutable del juego.
- [ ] Pruebas de balance por simulación: no realizadas.

## Limitación funcional detectada en las fuentes

No se encontró un sistema de recogida automática configurable por categorías en los árboles de código 1.0.3 ni 1.0.4 recuperado. Por tanto, no es posible certificar su conservación a partir de estos archivos. No se añadió una implementación nueva sin una especificación o una fuente que contenga esa función.

## Interpretación de la recompensa divina

La recompensa divina es un `ScrollOfMetamorphosis` original. Permite sustituir un talento por otro de otra clase en el mismo nivel de talento. No añade una ranura ni un punto adicional de talento. Esta elección reutiliza la interfaz, compatibilidad y persistencia originales del juego en vez de modificar directamente el árbol del héroe.

## Resultado

La fuente está integrada y pasa las verificaciones estáticas indicadas. No debe etiquetarse como compilada, probada en Android ni validada en ejecución hasta completar las comprobaciones pendientes en un entorno con Gradle, Android SDK y el juego ejecutable.
