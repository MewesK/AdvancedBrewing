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
import advancedbrewing.tileentity.TileEntityBrewery;

public class ContainerBrewery extends ContainerMachine<TileEntityBrewery> {

	public ContainerBrewery(InventoryPlayer inventoryPlayer, TileEntityBrewery tileEntity) {
		super(inventoryPlayer, tileEntity, 22);

		// input slot
		this.addSlotToContainer(new SlotBreweryBasePotionContainer(tileEntity, 0, 8, 41));

		// output slot
		this.addSlotToContainer(new SlotBreweryEmptyContainer(tileEntity, 1, 152, 41));

		// ingredient slot(s)
		if (tileEntity.getType() > 0) {
			this.addSlotToContainer(new SlotBreweryPotionIngredientGhost(tileEntity, 2, 62, 20));
			this.addSlotToContainer(new SlotBreweryPotionIngredientGhost(tileEntity, 3, 80, 20));
			this.addSlotToContainer(new SlotBreweryPotionIngredientGhost(tileEntity, 4, 98, 20));
		}
		else {
			this.addSlotToContainer(new SlotBreweryPotionIngredientGhost(tileEntity, 2, 80, 20));
		}

		// ingredient buffer
		for (int i = 0; i < 9; i++) {
			this.addSlotToContainer(new SlotBreweryPotionIngredient(tileEntity, 5 + i, 8 + i * 18, 84));
		}
	}
}