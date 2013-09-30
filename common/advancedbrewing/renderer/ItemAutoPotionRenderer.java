/** 
 * Copyright (c) MewK, 2013
 * http://advancedbrewing.mewk.net
 * 
 * Advanced Brewing is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://advancedbrewing.mewk.net/MMPL-1.0.txt
 */

package advancedbrewing.renderer;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Icon;

public class ItemAutoPotionRenderer extends ItemGlintOverlayRenderer {
	@Override
	protected Icon getIcon(ItemStack itemStack, int pass) {
		Item item = itemStack.getItem();
		NBTTagCompound tag = itemStack.getTagCompound();
		boolean activated = tag != null ? tag.getBoolean("Activated") : false;
		return item.getIcon(itemStack, pass == 1 && activated ? 2 : pass);
	}
}
