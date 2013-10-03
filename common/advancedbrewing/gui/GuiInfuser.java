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
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidTank;
import advancedbrewing.tileentity.TileEntityInfuser;
import advancedbrewing.tileentity.TileEntityMachine;
import advancedbrewing.utils.Localization;

public class GuiInfuser extends GuiMachine<TileEntityInfuser> {

	public GuiInfuser(InventoryPlayer inventoryPlayer, TileEntityInfuser tileEntity) {
		super(inventoryPlayer, tileEntity, new ContainerInfuser(inventoryPlayer, tileEntity), new ResourceLocation("advancedbrewing", "textures/gui/infuser.png"), Localization.get("gui.info.infuser.text"));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
		super.drawGuiContainerBackgroundLayer(par1, par2, par3);
		
		int x = (this.width - this.xSize) / 2;
		int y = (this.height - this.ySize) / 2;
		FluidTank[] fluidTanks = this.tileEntity.getFluidTanks();

		int fluidAmount = fluidTanks[0].getFluidAmount();
		if (fluidAmount > 0) {
			float gaugeHeight = (((float) fluidAmount) / ((float) TileEntityMachine.MAX_FLUIDAMOUNT)) * 58f;
			this.displayFluidGauge(x, y, 20, 80, (int) gaugeHeight, fluidTanks[0].getFluid());
		}
	}
}