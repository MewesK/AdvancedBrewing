/** 
 * Copyright (c) MewK, 2013
 * http://advancedbrewing.mewk.net
 * 
 * Advanced Brewing is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://advancedbrewing.mewk.net/MMPL-1.0.txt
 */

package advancedbrewing.gui;

import net.minecraft.inventory.IInventory;

public class SlotBreweryPotionIngredientGhost extends SlotBreweryPotionIngredient {

	public SlotBreweryPotionIngredientGhost(IInventory par2IInventory, int par3, int par4, int par5) {
		super(par2IInventory, par3, par4, par5);
	}

	@Override
	public int getSlotStackLimit() {
		return 1;
	}
}