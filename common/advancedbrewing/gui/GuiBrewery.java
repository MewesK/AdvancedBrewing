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
import advancedbrewing.tileentity.TileEntityBrewery;
import advancedbrewing.tileentity.TileEntityMachine;
import advancedbrewing.utils.Localization;

public class GuiBrewery extends GuiMachine<TileEntityBrewery> {

	public GuiBrewery(InventoryPlayer inventoryPlayer, TileEntityBrewery tileEntity) {
		super(inventoryPlayer, tileEntity, new ContainerBrewery(inventoryPlayer, tileEntity), new ResourceLocation("advancedbrewing", tileEntity.getType() > 0 ? "textures/gui/brewery_multi.png" : "textures/gui/brewery.png"), Localization.get("gui.info.brewery.text"));
		this.ySize = 188;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
		super.drawGuiContainerBackgroundLayer(par1, par2, par3);

		int x = (this.width - this.xSize) / 2;
		int y = (this.height - this.ySize) / 2;
		FluidTank[] fluidTanks = this.tileEntity.getFluidTanks();
		TileEntityBrewery tileEntity = this.tileEntity;

		int workTime = tileEntity.getWorkTime() > 0 ? tileEntity.getWorkTime() : TileEntityBrewery.MAX_WORKTIME;
		if (workTime > 0) {
			float gaugeWidth = (((float) (TileEntityBrewery.MAX_WORKTIME - workTime)) / ((float) TileEntityBrewery.MAX_WORKTIME)) * 29f;
			this.drawTexturedModalRect(x + 73, y + 42, 192, 0, (int) gaugeWidth, 12);
		}

		int inputFluidAmount = fluidTanks[0].getFluidAmount();
		if (inputFluidAmount > 0) {
			float gaugeHeight = (((float) inputFluidAmount) / ((float) TileEntityMachine.MAX_FLUIDAMOUNT)) * 58f;
			this.displayFluidGauge(x, y, 20, 34, (int) gaugeHeight, fluidTanks[0].getFluid());
		}

		int outputFluidAmount = fluidTanks[1].getFluidAmount();
		if (outputFluidAmount > 0) {
			float gaugeHeight = (((float) outputFluidAmount) / ((float) TileEntityMachine.MAX_FLUIDAMOUNT)) * 58f;
			this.displayFluidGauge(x, y, 20, 126, (int) gaugeHeight, fluidTanks[1].getFluid());
		}
	}
}