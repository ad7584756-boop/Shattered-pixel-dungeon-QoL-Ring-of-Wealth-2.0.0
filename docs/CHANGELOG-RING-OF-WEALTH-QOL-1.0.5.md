# Ring of Wealth QoL 1.0.5

## Ring of Wealth rarity progression

Equipment rewards now use a level-based rarity distribution:

| Ring level | Common | Rare | Epic | Legendary | Mystic | Divine |
|---|---:|---:|---:|---:|---:|---:|
| +0 to +4 | 60% | 30% | 7% | 3% | 0% | 0% |
| +5 to +9 | 45% | 35% | 15% | 5% | 0% | 0% |
| +10 to +14 | 25% | 40% | 25% | 10% | 0% | 0% |
| +15 to +19 | 0% | 20% | 60% | 19.8% | 0.2% | 0% |
| +20 to +99 | 0% | 0% | 60% | 39.8% | 0.19% | 0.01% |
| +100 to +499 | 0% | 0% | 60% | 39.79% | 0.19% | 0.02% |
| +500 to +999 | 0% | 0% | 60% | 39.76% | 0.19% | 0.05% |
| +1000 and above | 0% | 0% | 60% | 39.72% | 0.19% | 0.09% |

The epic category remains at 60% in the high-level bands. Common rewards disappear at +15 and rare rewards disappear at +20. Mystic and divine chances are rolled per reward, including consumable reward events; the original consumable low/mid/high progression formula remains intact. Divine drops are suppressed when the hero already has a second branch or the No Scrolls challenge is active.

- Mystic rewards are real signature items from one of the other five hero classes.
- Divine rewards are a unique Divine Branch scroll. The player chooses one of the other five classes.
- The divine branch is saved with the hero, appears beneath the primary tree in the Talents pane, and has its own tier-matched talent-point pool. The primary branch is not replaced.
- Class-specific talent checks were adapted where needed so transferred talents can function outside their original hero class.
- Mystic flare is bright red. Divine flare is a large dark-violet burst with a bright core. Existing green, cyan, purple, and orange flares remain.
- The existing Wealth multiplier formula and original consumable progression are unchanged.

## Badder Bosses adjustments

When Stronger/Badder Bosses is enabled:

- Goo: 150 HP.
- Tengu: 350 HP and 1.25x action speed.
- DM-300: all four energy pylons must be activated and destroyed.
- Dwarf King: 700 HP. His minions remain their normal types but scale to 1.5x stats in phase 2 and 2x stats in phase 3.
- Yog-Dzewa: 200,000 HP; four fists at each phase transition and six in the final phase; minions arrive 1.5x faster in double bursts.

## OVERKILL DOOM MODE

A separate, red-highlighted challenge:

- Goo: 1,500 HP and double speed.
- Tengu: 3,500 HP and double action speed.
- DM-300: 25,000 HP, corrosive gas, and four required energy pylons.
- Dwarf King: 500,000 HP; summons red Growing champions with at least 500 additional HP each.
- Yog-Dzewa: 100,000,000 HP and the red Growing champion effect. Its health, attack, and defense increase over time.
- Yog summons eight fists per phase; each fist has 100,000 HP and at least 1,001 damage. Fists keep their explicit stats and do not receive the universal floor multiplier.
- Yog summons three monsters per burst and its summon timer advances three times faster.
- Ordinary enemy mobs receive floor-based HP, damage, and evasion multipliers: x5 (floors 1–5), x10 (6–10), x20 (11–15), x35 (16–20), and x50 (21–25). Bosses retain their explicit challenge stats.

## GOD'S CHOSEN ONE

A separate ascension challenge. While the Ascension Challenge is active, every newly generated enemy is a red Growing champion and the respawn cooldown is reduced to one third.

## Final achievements

- **DESTROYER OF DOOM**: win the run after killing Yog-Dzewa in OVERKILL DOOM MODE and earning at least 10,000,000 total experience during that run.
- **GOD'S CHOSEN ONE**: complete the Amulet ascension while the GOD'S CHOSEN ONE challenge is active.

Both achievements have custom red-framed icons and are explicitly placed as the final two entries in the global achievement list.

## Build workflow

`.github/workflows/build-android-debug.yml` provides a manual GitHub Actions workflow to build and upload the installable debug APK artifact.
