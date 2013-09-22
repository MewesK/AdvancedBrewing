package advancedbrewing.gui;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidTank;

import advancedbrewing.entity.TileEntityBrewery;
import advancedbrewing.entity.TileEntityMachine;

public class GuiBrewery extends GuiMachine<TileEntityBrewery> {

	public GuiBrewery(InventoryPlayer inventoryPlayer, TileEntityBrewery tileEntity) {
		super(inventoryPlayer, tileEntity, new ContainerBrewery(inventoryPlayer, tileEntity), new ResourceLocation("advancedbrewing", "textures/gui/brewery.png"));
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
			drawTexturedModalRect(x + 73, y + 42, 192, 0, (int) gaugeWidth, 12);
		}

		int inputFluidAmount = fluidTanks[0].getFluidAmount();
		if (inputFluidAmount > 0) {
			float gaugeHeight = (((float) inputFluidAmount) / ((float) TileEntityMachine.MAX_FLUIDAMOUNT)) * 58f;
			this.displayFluidGauge(x, y, 20, 44, (int) gaugeHeight, fluidTanks[0].getFluid());
		}

		int outputFluidAmount = fluidTanks[1].getFluidAmount();
		if (outputFluidAmount > 0) {
			float gaugeHeight = (((float) outputFluidAmount) / ((float) TileEntityMachine.MAX_FLUIDAMOUNT)) * 58f;
			this.displayFluidGauge(x, y, 20, 116, (int) gaugeHeight, fluidTanks[1].getFluid());
		}
	}
}