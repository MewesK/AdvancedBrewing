/** 
 * Copyright (c) MewK, 2013
 * http://advancedbrewing.mewk.net
 * 
 * Advanced Brewing is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://advancedbrewing.mewk.net/MMPL-1.0.txt
 */

package advancedbrewing;

import net.minecraftforge.fluids.Fluid;

public class FluidPotion extends Fluid {

	private int color = 0xFFFFFF;

	public FluidPotion(String fluidName) {
		super(fluidName);
	}

	public FluidPotion(String fluidName, int color) {
		super(fluidName);
		this.color = color;
	}

	@Override
	public int getColor() {
		return this.color;
	}

	public void setColor(int color) {
		this.color = 0xFFFFFF;
	}
}