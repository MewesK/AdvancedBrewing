/*
 * Copyright (c) SpaceToad, 2011-2012
 * http://www.mod-buildcraft.com
 * 
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package buildcraft.api.core;

import net.minecraft.item.ItemStack;

/**
 * 
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class StackWrapper {

	public final ItemStack stack;

	public StackWrapper(ItemStack stack) {
		this.stack = stack;
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 67 * hash + this.stack.itemID;
		hash = 67 * hash + this.stack.getItemDamage();
		if (this.stack.stackTagCompound != null) {
			hash = 67 * hash + this.stack.stackTagCompound.hashCode();
		}
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final StackWrapper other = (StackWrapper) obj;
		if (this.stack.itemID != other.stack.itemID) {
			return false;
		}
		if (this.stack.getHasSubtypes() && this.stack.getItemDamage() != other.stack.getItemDamage()) {
			return false;
		}
		if (this.stack.stackTagCompound != null && !this.stack.stackTagCompound.equals(other.stack.stackTagCompound)) {
			return false;
		}
		return true;
	}
}
