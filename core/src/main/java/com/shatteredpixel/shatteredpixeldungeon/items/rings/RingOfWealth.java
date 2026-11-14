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
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.items.rings;

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.CounterBuff;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.BrokenSeal;
import com.shatteredpixel.shatteredpixeldungeon.items.Honeypot;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CloakOfShadows;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HolyTome;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfExperience;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.UnstableBrew;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.ExoticPotion;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.PotionOfDivineInspiration;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTransmutation;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfDivineBranch;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ExoticScroll;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfMetamorphosis;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.UnstableSpell;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfEnchantment;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.ExoticCrystals;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Rapier;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfMagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.Visual;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.ArrayList;

public class RingOfWealth extends Ring {
	// QoL 1.0.3: the wealth bonus grows by 0.1x every three effective levels.
	private static float wealthLevelMultiplier(int n) {
		return 4.0f + 0.1f * (float)Math.floor(Math.max(0, n) / 3.0);
	}

	private static double fractionalPart(double value) {
		return value - Math.floor(value);
	}

	private static double fibonacci(int n) {
		if (n <= 0) return 0.0;
		if (n == 1) return 1.0;
		double previous = 0.0;
		double current = 1.0;
		for (int i = 2; i <= n; i++) {
			if (current >= Double.MAX_VALUE - previous) return Double.MAX_VALUE;
			double next = previous + current;
			previous = current;
			current = next;
		}
		return current;
	}

	/** P(n) = 589.69 * C(n) * 10^E(n), with C(n)=A(n)/A(5). */
	private static double wealthLuckPercent(int n) {
		double a = 1.0 + 9.0 * fractionalPart(Math.sqrt(2.0) * n);
		double a5 = 1.0 + 9.0 * fractionalPart(Math.sqrt(2.0) * 5.0);
		double exponent = n <= 5 ? n - 5 : fibonacci(n - 3) - 1.0;
		return 589.69 * (a / a5) * Math.pow(10.0, exponent);
	}

	private static String formatWealthLuckPercent(int level) {
		double value = wealthLuckPercent(level);
		if (Double.isInfinite(value) || value >= 1.0e9) {
			return Messages.decimalFormat("0.######E0", value);
		}
		return Messages.decimalFormat("0.######", value);
	}

	{
		icon = ItemSpriteSheet.Icons.RING_WEALTH;
		buffClass = Wealth.class;
	}

	private float triesToDrop = Float.MIN_VALUE;
	private int dropsToRare = Integer.MIN_VALUE;
	
	public String statsInfo() {
		if (isIdentified()){
			String info = Messages.get(this, "stats",
					formatWealthLuckPercent(soloBuffedBonus()));
			if (isEquipped(Dungeon.hero) && soloBuffedBonus() != combinedBuffedBonus(Dungeon.hero)){
				info += "\n\n" + Messages.get(this, "combined_stats",
						formatWealthLuckPercent(combinedBuffedBonus(Dungeon.hero)));
			}
			return info;
		} else {
			return Messages.get(this, "typical_stats", Messages.decimalFormat("#.##", 20f));
		}
	}

	public String upgradeStat1(int level){
		if (cursed && cursedKnown) level = Math.min(-1, level-3);
		return formatWealthLuckPercent(level + 1) + "%";
	}

	private static final String TRIES_TO_DROP = "tries_to_drop";
	private static final String DROPS_TO_RARE = "drops_to_rare";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(TRIES_TO_DROP, triesToDrop);
		bundle.put(DROPS_TO_RARE, dropsToRare);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		triesToDrop = bundle.getFloat(TRIES_TO_DROP);
		dropsToRare = bundle.getInt(DROPS_TO_RARE);
	}

	@Override
	protected RingBuff buff( ) {
		return new Wealth();
	}
	
	public static float dropChanceMultiplier( Char target ){
		int n = getBuffedBonus(target, Wealth.class);
		return 1.0f + (float)(wealthLuckPercent(n) * wealthLevelMultiplier(n) / 100.0);
	}
	
	public static ArrayList<Item> tryForBonusDrop(Char target, int tries ){
		int bonus = getBuffedBonus(target, Wealth.class);
		if (bonus <= 0) return null;
		return tryForBonusDropWithWealthLevel(target, Math.round(tries * wealthLevelMultiplier(bonus)), bonus, null);
	}

	/**
	 * Like {@link #tryForBonusDrop(Char, int)} but uses {@code forcedWealthBonus} for reward tiers
	 * without requiring an equipped ring. {@code equipBonusOverride} is used for equipment drops
	 * (simulates a single ring at that level); consumable tier uses {@code forcedWealthBonus}.
	 */
	public static ArrayList<Item> tryForBonusDrop(Char target, int tries, int forcedWealthBonus ){
		if (forcedWealthBonus <= 0) return null;
		return tryForBonusDropWithWealthLevel(target, tries, forcedWealthBonus, forcedWealthBonus);
	}

	/** @param equipBonusForGear if non-null, used for {@link #genEquipmentDrop(int)}; if null, summed from equipped Wealth buffs */
	private static ArrayList<Item> tryForBonusDropWithWealthLevel(Char target, int tries, int wealthBonus, Integer equipBonusForGear ){
		CounterBuff triesToDrop = target.buff(TriesToDropTracker.class);
		if (triesToDrop == null){
			triesToDrop = Buff.affect(target, TriesToDropTracker.class);
			triesToDrop.countUp( Random.NormalIntRange(0, 20) );
		}

		CounterBuff dropsToEquip = target.buff(DropsToEquipTracker.class);
		if (dropsToEquip == null){
			dropsToEquip = Buff.affect(target, DropsToEquipTracker.class);
			dropsToEquip.countUp( Random.NormalIntRange(5, 10) );
		}

		ArrayList<Item> drops = new ArrayList<>();

		triesToDrop.countDown(tries);
		while ( triesToDrop.count() <= 0 ){
			if (dropsToEquip.count() <= 0){
				int equipBonus;
				if (equipBonusForGear != null){
					equipBonus = equipBonusForGear;
				} else {
					equipBonus = 0;
					//A second ring of wealth can be at most +1 when calculating wealth bonus for equips
					//This is to prevent using an upgraded wealth to farm another upgraded wealth and
					//using the two to get substantially more upgrade value than intended
					for (Wealth w : target.buffs(Wealth.class)){
						if (w.buffedLvl() > equipBonus){
							equipBonus = w.buffedLvl() + Math.min(equipBonus, 2);
						} else {
							equipBonus += Math.min(w.buffedLvl(), 2);
						}
					}
				}

				Item i;
				do {
					i = genEquipmentDrop(equipBonus - 1);
				} while (Challenges.isItemBlocked(i));
				drops.add(i);
				dropsToEquip.countUp(Random.NormalIntRange(5, 10));
			} else {
				Item i;
				do {
					i = genConsumableDrop(wealthBonus - 1);
				} while (Challenges.isItemBlocked(i));
				drops.add(i);
				dropsToEquip.countDown(1);
			}
			triesToDrop.countUp( Random.NormalIntRange(0, 20) );
		}

		return drops;
	}

	//used for visuals
	// 1/2/3 used for low/mid/high tier consumables
	// 3 used for +0-1 equips, 4 used for +2 or higher equips
	private static int latestDropTier = 0;

	public static void showFlareForBonusDrop( Visual vis ){
		if (vis == null || vis.parent == null) return;
		switch (latestDropTier){
			default:
				break; //do nothing
			case 1:
				new Flare(6, 20).color(0x00FF00, true).show(vis, 3f);
				break;
			case 2:
				new Flare(6, 24).color(0x00AAFF, true).show(vis, 3.33f);
				break;
			case 3:
				new Flare(6, 28).color(0xAA00FF, true).show(vis, 3.67f);
				break;
			case 4:
				new Flare(6, 32).color(0xFFAA00, true).show(vis, 4f);
				break;
			case 5:
				new Flare(8, 42).color(0xFF142B, true).show(vis, 5f);
				break;
			case 6:
				new Flare(10, 52).color(0x281039, true).show(vis, 6f);
				new Flare(6, 30).color(0xE8D8FF, true).show(vis, 4f);
				break;
		}
		latestDropTier = 0;
	}
	
	public static Item genConsumableDrop(int level) {
		Item special = genHigherRarityReward(level);
		if (special != null) return special;
		float roll = Random.Float();
		//60% chance - 4% per level. Starting from +15: 0%
		if (roll < (0.6f - 0.04f * level)) {
			latestDropTier = 1;
			return genLowValueConsumable();
		//30% chance + 2% per level. Starting from +15: 60%-2%*(lvl-15)
		} else if (roll < (0.9f - 0.02f * level)) {
			latestDropTier = 2;
			return genMidValueConsumable();
		//10% chance + 2% per level. Starting from +15: 40%+2%*(lvl-15)
		} else {
			latestDropTier = 3;
			return genHighValueConsumable();
		}
	}

	private static Item genLowValueConsumable(){
		switch (Random.Int(4)){
			case 0: default:
				Item i = new Gold().random();
				return i.quantity(i.quantity()/2);
			case 1:
				return Generator.randomUsingDefaults(Generator.Category.STONE);
			case 2:
				return Generator.randomUsingDefaults(Generator.Category.POTION);
			case 3:
				return Generator.randomUsingDefaults(Generator.Category.SCROLL);
		}
	}

	private static Item genMidValueConsumable(){
		switch (Random.Int(6)){
			case 0: default:
				Item i = genLowValueConsumable();
				return i.quantity(i.quantity()*2);
			case 1:
				i = Generator.randomUsingDefaults(Generator.Category.POTION);
				if (!(i instanceof ExoticPotion)) {
					return Reflection.newInstance(ExoticPotion.regToExo.get(i.getClass()));
				} else {
					return Reflection.newInstance(i.getClass());
				}
			case 2:
				i = Generator.randomUsingDefaults(Generator.Category.SCROLL);
				if (!(i instanceof ExoticScroll)){
					return Reflection.newInstance(ExoticScroll.regToExo.get(i.getClass()));
				} else {
					return Reflection.newInstance(i.getClass());
				}
			case 3:
				return Random.Int(2) == 0 ? new UnstableBrew() : new UnstableSpell();
			case 4:
				return new Bomb();
			case 5:
				return new Honeypot();
		}
	}

	private static Item genHighValueConsumable(){
		switch (Random.Int(4)){
			case 0: default:
				Item i = genMidValueConsumable();
				if (i instanceof Bomb){
					return new Bomb.DoubleBomb();
				} else {
					return i.quantity(i.quantity()*2);
				}
			case 1:
				return new StoneOfEnchantment();
			case 2:
				return Random.Float() < ExoticCrystals.consumableExoticChance() ? new PotionOfDivineInspiration() : new PotionOfExperience();
			case 3:
				return Random.Float() < ExoticCrystals.consumableExoticChance() ? new ScrollOfMetamorphosis() : new ScrollOfTransmutation();
		}
	}

	/**
	 * Generates a reward for the Fortune armor glyph using the same reward tables
	 * as the Ring of Wealth. This deliberately does not alter the ring's drop
	 * chance formula or its persistent drop trackers.
	 */
	public static Item genFortuneReward( int level ){
		Item reward;
		do {
			reward = Random.Int(2) == 0 ? genConsumableDrop(level) : genEquipmentDrop(level);
		} while (Challenges.isItemBlocked(reward));
		return reward;
	}

	/**
	 * Rolls the equipment rarity distribution for the effective Wealth level.
	 * Common and rare rewards disappear at high levels; epic remains the dominant
	 * category once the ring reaches its final progression band.
	 */
	private static Item genHigherRarityReward(int level){
		int effectiveLevel = Math.max(0, level);
		if (effectiveLevel < 15) return null;
		float mysticalChance = effectiveLevel < 20 ? 0.002f : 0.0019f;
		float divineChance = 0f;
		if (effectiveLevel >= 1000) divineChance = 0.0009f;
		else if (effectiveLevel >= 500) divineChance = 0.0005f;
		else if (effectiveLevel >= 100) divineChance = 0.0002f;
		else if (effectiveLevel >= 20) divineChance = 0.0001f;
		if ((Dungeon.hero != null && Dungeon.hero.divineTalentClass != null)
				|| Dungeon.isChallenged(Challenges.NO_SCROLLS)) divineChance = 0f;
		float roll = Random.Float();
		if (roll < divineChance){
			latestDropTier = 6;
			return new ScrollOfDivineBranch();
		}
		if (roll < divineChance + mysticalChance) return genMysticalReward(level);
		return null;
	}

	private static int rollEquipmentRarity(int level){
		int effectiveLevel = Math.max(0, level);
		float common, rare, epic, mystical, divine;
		if (effectiveLevel < 5){
			common = 0.60f; rare = 0.30f; epic = 0.07f; mystical = 0f; divine = 0f;
		} else if (effectiveLevel < 10){
			common = 0.45f; rare = 0.35f; epic = 0.15f; mystical = 0f; divine = 0f;
		} else if (effectiveLevel < 15){
			common = 0.25f; rare = 0.40f; epic = 0.25f; mystical = 0f; divine = 0f;
		} else if (effectiveLevel < 20){
			common = 0f; rare = 0.20f; epic = 0.60f; mystical = 0.002f; divine = 0f;
		} else {
			common = 0f; rare = 0f; epic = 0.60f; mystical = 0.0019f;
			if (effectiveLevel >= 1000) divine = 0.0009f;
			else if (effectiveLevel >= 500) divine = 0.0005f;
			else if (effectiveLevel >= 100) divine = 0.0002f;
			else divine = 0.0001f;
			// A hero can only select one additional class branch per run.
			if ((Dungeon.hero != null && Dungeon.hero.divineTalentClass != null)
					|| Dungeon.isChallenged(Challenges.NO_SCROLLS)) divine = 0f;
		}
		float legendary = 1f - common - rare - epic - mystical - divine;
		float roll = Random.Float();
		if (roll < common) return 1;
		roll -= common;
		if (roll < rare) return 2;
		roll -= rare;
		if (roll < epic) return 3;
		roll -= epic;
		if (roll < legendary) return 4;
		roll -= legendary;
		if (roll < mystical) return 5;
		return 6;
	}

	/** Returns an authentic signature item belonging to a different hero class. */
	private static Item genMysticalReward( int level ){
		com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass[] classes = com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass.values();
		ArrayList<com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass> otherClasses = new ArrayList<>();
		for (com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass cls : classes){
			if (cls != Dungeon.hero.heroClass) otherClasses.add(cls);
		}
		if (otherClasses.isEmpty()) return null;
		com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass chosen = Random.element(otherClasses);
		Item reward;
		switch (chosen){
			case WARRIOR: reward = new BrokenSeal(); break;
			case MAGE: reward = new MagesStaff(new WandOfMagicMissile()); break;
			case ROGUE: reward = new CloakOfShadows(); break;
			case HUNTRESS: reward = new SpiritBow(); break;
			case DUELIST: reward = new Rapier(); break;
			case CLERIC: reward = new HolyTome(); break;
			default: return null;
		}
		if (reward instanceof Weapon && reward.isUpgradable()){
			int minLevel = Math.max(0, level + 1);
			if (reward.level() < minLevel) reward.level(minLevel);
		}
		if (reward instanceof MagesStaff) ((MagesStaff) reward).updateWand(false);
		reward.cursed = false;
		reward.cursedKnown = true;
		latestDropTier = 5;
		return reward;
	}

	private static Item genEquipmentDrop( int level ){
		int rarity = rollEquipmentRarity(level);
		if (rarity == 5){
			Item mystical = genMysticalReward(level);
			if (mystical != null) return mystical;
			rarity = 4;
		}
		if (rarity == 6){
			latestDropTier = 6;
			return new ScrollOfDivineBranch();
		}

		Item result;
		//each upgrade increases depth used for calculating drops by 1
		int floorset = (Dungeon.depth + level)/5;
		switch (Random.Int(7)){
			default: case 0: case 1:
				Weapon w = Generator.randomWeapon(floorset, true);
				if (!w.hasGoodEnchant() && Random.Int(10) < level)      w.enchant();
				else if (w.hasCurseEnchant())                           w.enchant(null);
				result = w;
				break;
			case 2:
				Armor a = Generator.randomArmor(floorset);
				if (!a.hasGoodGlyph() && Random.Int(10) < level)        a.inscribe();
				else if (a.hasCurseGlyph())                             a.inscribe(null);
				result = a;
				break;
			case 3:
				result = Generator.randomUsingDefaults(Generator.Category.WAND);
				break;
			case 4:
				result = Generator.randomUsingDefaults(Generator.Category.RING);
				break;
			case 5:
				result = Generator.randomMissile(floorset, true);
				break;
			case 6:
				result = Generator.random(Generator.Category.ARTIFACT);
				break;
		}
		// Weapons (including thrown weapons), armor, wands and rings respect the
		// 1.0.4 minimum: effective Wealth level + 1. Artifacts retain the old floor.
		if (result.isUpgradable()){
			int minLevel = (result instanceof Weapon || result instanceof Armor
					|| result instanceof Wand || result instanceof Ring) ? level + 1 : (level+1)/2;
			int rarityBonus = rarity - 1;
			if (result.level() < minLevel + rarityBonus){
				result.level(minLevel + rarityBonus);
			}
		}
		if (rarity >= 3 && result instanceof Weapon){
			Weapon weapon = (Weapon)result;
			if (!weapon.hasGoodEnchant()) weapon.enchant();
			if (weapon.hasCurseEnchant()) weapon.enchant(null);
		} else if (rarity >= 3 && result instanceof Armor){
			Armor armor = (Armor)result;
			if (!armor.hasGoodGlyph()) armor.inscribe();
			if (armor.hasCurseGlyph()) armor.inscribe(null);
		}
		result.cursed = false;
		result.cursedKnown = true;
		latestDropTier = rarity;
		return result;
	}

	public class Wealth extends RingBuff {
	}

	public static class TriesToDropTracker extends CounterBuff {
		{
			revivePersists = true;
		}
	}

	public static class DropsToEquipTracker extends CounterBuff {
		{
			revivePersists = true;
		}
	}
}
