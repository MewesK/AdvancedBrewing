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
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import advancedbrewing.AdvancedBrewing;

public class SlotBreweryPotionContainer extends Slot {
	public SlotBreweryPotionContainer(IInventory par2IInventory, int par3, int par4, int par5) {
		super(par2IInventory, par3, par4, par5);
	}

	@Override
	public boolean isItemValid(ItemStack par1ItemStack) {
		return SlotBreweryPotionContainer.isItemValidForSlot(par1ItemStack);
	}

	@Override
	public int getSlotStackLimit() {
		return 1;
	}

	public static boolean isItemValidForSlot(ItemStack par1ItemStack) {
		if (par1ItemStack != null) {
			Item item = par1ItemStack.getItem();
			return item.itemID == AdvancedBrewing.bucketPotionItem.itemID || item.itemID == Item.potion.itemID;
		}
		return false;
	}
}
