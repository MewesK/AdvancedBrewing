/** 
 * Copyright (c) MewK, 2013
 * http://advancedbrewing.mewk.net
 * 
 * Advanced Brewing is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://advancedbrewing.mewk.net/MMPL-1.0.txt
 */

package advancedbrewing.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import advancedbrewing.tileentity.TileEntityVaporizer;

public class ContainerVaporizer extends ContainerMachine<TileEntityVaporizer> {

	protected int lastRadius;
	
	public ContainerVaporizer(InventoryPlayer inventoryPlayer, TileEntityVaporizer tileEntity) {
		super(inventoryPlayer, tileEntity, 0);

		// input slot
		this.addSlotToContainer(new SlotBreweryPotionContainer(tileEntity, 0, 54, 41));
	}
	
	@Override
	public void detectAndSendChanges() {
		
		// send updated inventory
		
		super.detectAndSendChanges();

		// send updated custom values

		int radius = this.tileEntity.getRadius();
		
		for (int i = 0; i < this.crafters.size(); ++i) {
			ICrafting icrafting = (ICrafting) this.crafters.get(i);

			if (this.lastRadius != radius) {
				icrafting.sendProgressBarUpdate(this, 5, radius);
			}
		}

		// save updated values
		
		this.lastRadius = radius;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int value) {
		// set properties
		switch (id) {
			case 5:
				this.tileEntity.setRadius(value);
				return;
		}
	}
}