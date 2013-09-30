/** 
 * Copyright (c) MewK, 2013
 * http://advancedbrewing.mewk.net
 * 
 * Advanced Brewing is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://advancedbrewing.mewk.net/MMPL-1.0.txt
 */

package advancedbrewing.gui;

import net.minecraft.entity.player.InventoryPlayer;
import advancedbrewing.tileentity.TileEntityVaporizer;

public class ContainerVaporizer extends ContainerMachine<TileEntityVaporizer> {

	public ContainerVaporizer(InventoryPlayer inventoryPlayer, TileEntityVaporizer tileEntity) {
		super(inventoryPlayer, tileEntity, 0);

		// input slot
		this.addSlotToContainer(new SlotBreweryPotionContainer(tileEntity, 0, 54, 41));
	}
}