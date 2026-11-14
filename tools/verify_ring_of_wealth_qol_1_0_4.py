#!/usr/bin/env python3
"""Static integrity checks for Ring of Wealth QoL 1.0.4.

Run from any directory: python tools/verify_ring_of_wealth_qol_1_0_4.py
These checks do not replace Gradle compilation or in-game testing.
"""
from pathlib import Path
import re
import sys

ROOT = Path(__file__).resolve().parents[1]
JAVA = ROOT / "core/src/main/java/com/shatteredpixel/shatteredpixeldungeon"

def read(rel):
    return (ROOT / rel).read_text(encoding="utf-8")

def check(condition, message):
    if not condition:
        raise AssertionError(message)
    print("PASS:", message)

ring = read("core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/rings/RingOfWealth.java")
command = read("core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/Command.java")
armor = read("core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/armor/Armor.java")
hero_class = read("core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/HeroClass.java")

check("return 4.0f + 0.1f * (float)Math.floor(Math.max(0, n) / 3.0);" in ring,
      "multiplicador original x4,0 y +0,1 cada tres niveles")
check("589.69 * (a / a5) * Math.pow(10.0, exponent)" in ring,
      "fórmula original de suerte conservada")
check("Math.round(tries * wealthLevelMultiplier(bonus))" in ring,
      "multiplicador original aplicado al contador de intentos")

for category in ["randomWeapon(floorset, true)", "randomArmor(floorset)",
                 "Generator.Category.WAND", "Generator.Category.RING",
                 "randomMissile(floorset, true)", "Generator.Category.ARTIFACT"]:
    check(category in ring, f"generación de categoría: {category}")

for name in ["BrokenSeal", "MagesStaff", "WandOfMagicMissile", "CloakOfShadows",
             "SpiritBow", "Rapier", "HolyTome", "ScrollOfMetamorphosis"]:
    check(name in ring, f"recompensa mística/divina usa objeto original: {name}")

check("case WARRIOR:" in ring and "case MAGE:" in ring and "case ROGUE:" in ring
      and "case HUNTRESS:" in ring and "case DUELIST:" in ring and "case CLERIC:" in ring,
      "hay asignación de objeto para los seis héroes")
for name in ["new BrokenSeal()", "new MagesStaff(new WandOfMagicMissile())", "new CloakOfShadows()",
             "new SpiritBow()", "new Rapier()", "new HolyTome()"]:
    check(name in hero_class, f"objeto distintivo confirmado en el inicio original: {name}")

check("Math.min(0.01f, 0.001f + 0.00025f * effectiveLevel)" in ring,
      "probabilidad divina creciente y limitada")
check("Math.min(0.04f, 0.005f + 0.001f * effectiveLevel)" in ring,
      "probabilidad mística creciente y limitada")
check("case 5:" in ring and "case 6:" in ring and "latestDropTier = 6" in ring,
      "rarezas mística y divina tienen efectos visuales propios")
check("Fortune.class" in armor and (JAVA / "items/armor/glyphs/Fortune.java").is_file(),
      "glifo Fortune integrado y registrado")

for name in ["Potion", "Scroll", "Plant.Seed", "Runestone", "Bomb", "Honeypot",
             "ArcaneResin", "LiquidMetal", "Stylus", "Spell", "Food", "TippedDart"]:
    check(f"item instanceof {name}" in command, f"Command universal incluye {name}")
check("list.add(ScrollOfUpgrade.class)" in command,
      "Command añade Pergamino de Mejora como opción universal")
check("Challenges.NO_SCROLLS" in command,
      "Command conserva la restricción NO_SCROLLS")

build = read("build.gradle")
check("appVersionCode = 923" in build and "appVersionName = '1.0.4-QoL'" in build,
      "metadatos de versión 1.0.4-QoL")

print("\nStatic checks completed. Gradle and in-game tests are still required.")
