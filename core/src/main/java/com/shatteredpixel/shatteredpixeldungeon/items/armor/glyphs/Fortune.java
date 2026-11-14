/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfWealth;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.utils.Random;

/** Armor glyph which occasionally produces loot from the Ring of Wealth tables. */
public class Fortune extends Armor.Glyph {

    private static final ItemSprite.Glowing GOLD = new ItemSprite.Glowing(0xFFD700);

    @Override
    public int proc(Armor armor, Char attacker, Char defender, int damage) {
        if (!(defender instanceof Hero)) return damage;

        Hero wearer = (Hero) defender;
        int level = Math.max(0, armor.buffedLvl());
        float activationChance = Math.min(1f, (0.15f + level * 0.03f) * procChanceMultiplier(defender));

        if (Random.Float() < activationChance) {
            int rewards = Random.IntRange(2, 4 + level / 2);
            for (int i = 0; i < rewards; i++) {
                Item reward = RingOfWealth.genFortuneReward(level);
                if (reward != null && !reward.collect(wearer.belongings.backpack)) {
                    if (wearer.pos >= 0 && Dungeon.level != null) {
                        Dungeon.level.drop(reward, wearer.pos);
                    }
                }
            }
        }
        return damage;
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return GOLD;
    }
}
