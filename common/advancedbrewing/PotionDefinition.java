/** 
 * Copyright (c) MewK, 2013
 * http://advancedbrewing.mewk.net
 * 
 * Advanced Brewing is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://advancedbrewing.mewk.net/MMPL-1.0.txt
 */

package advancedbrewing;

import net.minecraft.block.Block;
import net.minecraft.potion.PotionHelper;

public class PotionDefinition {

	public enum Type {
		red, green, blue, yellow, pink, turquoise, grey, darkred, darkgreen, darkblue, darkyellow, darkpink, darkturquoise, darkgrey
	}

	private String name;
	private int[] potionIDs;
	private Block block;

	public PotionDefinition(String name) {
		this(name, null);
	}

	public PotionDefinition(String name, int[] potionIDs) {
		this.name = name;
		this.potionIDs = potionIDs;
	}

	public int getColor() {
		return PotionHelper.func_77915_a(this.getPotionID(), false);
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPotionID() {
		return this.potionIDs != null ? this.potionIDs[0] : -1;
	}

	public int[] getPotionIDs() {
		return this.potionIDs;
	}

	public void setPotionIds(int[] potionIDs) {
		this.potionIDs = potionIDs;
	}

	public Block getBlock() {
		return this.block;
	}

	public void setBlock(Block block) {
		this.block = block;
	}
}