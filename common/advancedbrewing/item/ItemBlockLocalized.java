/** 
 * Copyright (c) MewK, 2013
 * http://advancedbrewing.mewk.net
 * 
 * Advanced Brewing is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://advancedbrewing.mewk.net/MMPL-1.0.txt
 */

package advancedbrewing.item;

import advancedbrewing.utils.Localization;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockLocalized extends ItemBlock {

	public ItemBlockLocalized(int id) {
		super(id);
	}

	@Override
	public String getItemDisplayName(ItemStack itemstack) {
		return Localization.get(getUnlocalizedName(itemstack));
	}
}