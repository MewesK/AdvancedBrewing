/** 
 * Copyright (c) MewK, 2013
 * http://advancedbrewing.mewk.net
 * 
 * Advanced Brewing is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://advancedbrewing.mewk.net/MMPL-1.0.txt
 */

package advancedbrewing.gui;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidTank;
import advancedbrewing.tileentity.TileEntityMachine;
import advancedbrewing.tileentity.TileEntityVaporizer;
import advancedbrewing.utils.Localization;

public class GuiVaporizer extends GuiMachine<TileEntityVaporizer> {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public GuiVaporizer(InventoryPlayer inventoryPlayer, TileEntityVaporizer tileEntity) {
		super(inventoryPlayer, tileEntity, new ContainerVaporizer(inventoryPlayer, tileEntity), new ResourceLocation("advancedbrewing", "textures/gui/vaporizer.png"), Localization.get("gui.info.vaporizer.text"));
		
		if (this.ledgerManager.ledgers.get(2) instanceof advancedbrewing.gui.GuiMachine.ConfigLedger) {
    		ConfigLedger configLedger = (advancedbrewing.gui.GuiMachine.ConfigLedger) this.ledgerManager.ledgers.get(2);
    		configLedger.buttonList.add(new GuiButtonIcon(1, 22, 42, tileEntity.getRadius() + 1, 1));
    		configLedger.maxHeight = 28 + configLedger.buttonList.size() * 20;
		}
	}

	@Override
    protected void actionPerformed(GuiButton guiButton) {
		super.actionPerformed(guiButton);
		if (guiButton.id == 1 && guiButton instanceof GuiButtonIcon) {
			int radius = tileEntity.getRadius();
			radius++;
			if (radius > 3) {
				radius = 1;
			} 
			tileEntity.setRadius(radius);
			((GuiButtonIcon) guiButton).iconIndexX = radius + 1;
			
            ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
            DataOutputStream dataoutputstream = new DataOutputStream(bytearrayoutputstream);

            try {
                dataoutputstream.writeInt(1);
                dataoutputstream.writeInt(this.tileEntity.getRadius());
                this.mc.getNetHandler().addToSendQueue(new Packet250CustomPayload("AdvancedBrewing", bytearrayoutputstream.toByteArray()));
            } catch (Exception exception) {
                exception.printStackTrace();
            }
		}
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