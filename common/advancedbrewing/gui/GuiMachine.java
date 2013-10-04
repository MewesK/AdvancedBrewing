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
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.util.Icon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import advancedbrewing.PotionDefinition;
import advancedbrewing.tileentity.TileEntityMachine;
import advancedbrewing.tileentity.TileEntityPowered;
import advancedbrewing.utils.Localization;
import advancedbrewing.utils.Utils;

public abstract class GuiMachine<T extends TileEntityMachine> extends GuiLedgered {
	
	protected class EnergyLedger extends Ledger {
		protected TileEntityPowered tileEntityPowered;

		public EnergyLedger(TileEntityPowered tileEntityPowered) {
			this.tileEntityPowered = tileEntityPowered;
			
			maxHeight = 94;
			overlayColor = 0xd46c1f;
			iconOffsetX = 0;
			iconOffsetY = 0;
		}

		@Override
		public void draw(int x, int y) {
			super.draw(x, y);
			
			if (!isFullyOpened()) {
				return;
			}
			
			fontRenderer.drawStringWithShadow(Localization.get("gui.energy.title"), x + 22, y + 8, headerColour);

			fontRenderer.drawStringWithShadow(Localization.get("gui.energy.currentInput.text") + ":", x + 22, y + 20, subheaderColour);
			fontRenderer.drawString(String.format("%.1f MJ/t", tileEntityPowered.getCurrentInput()), x + 22, y + 32, textColour);
			fontRenderer.drawStringWithShadow(Localization.get("gui.energy.stored.text") + ":", x + 22, y + 44, subheaderColour);
			fontRenderer.drawString(String.format("%2.1f MJ", tileEntityPowered.getPowerHandler().getEnergyStored()), x + 22, y + 56, textColour);
			fontRenderer.drawStringWithShadow(Localization.get("gui.energy.consumption.text") + ":", x + 22, y + 68, subheaderColour);
			fontRenderer.drawString(String.format("%3.2f MJ/t", tileEntityPowered.getRecentEnergyAverage()), x + 22, y + 80, textColour);

		}

		@Override
		public String getTooltip() {
			return String.format("%3.2f MJ/t", tileEntityPowered.getRecentEnergyAverage());
		}
	}
	protected class InfoLedger extends Ledger {
		protected List<String> info;
		
		@SuppressWarnings("unchecked")
		public InfoLedger(String info) {
			this.info = FMLClientHandler.instance().getClient().fontRenderer.listFormattedStringToWidth(info != null ? info : "", 96);
			
			maxHeight = 28 + this.info.size() * 12;
			overlayColor = 0x085ca1;
			iconOffsetX = 1;
			iconOffsetY = 0;
		}

		@Override
		public void draw(int x, int y) {	
			super.draw(x, y);
			
			if (!isFullyOpened()) {
				return;
			}
			
			fontRenderer.drawStringWithShadow(Localization.get("gui.info.title"), x + 22, y + 8, headerColour);
			
			int yOffset = 0;
			for (String info : this.info) {
				fontRenderer.drawString(info, x + 22, y + 20 + yOffset, textColour);
				yOffset += 12;
			}
		}

		@Override
		public String getTooltip() {
			return Localization.get("gui.info.title");
		}
	}
	protected class ConfigLedger extends Ledger {
		protected TileEntityMachine tileEntityMachine;

		public ConfigLedger(TileEntityMachine tileEntityMachine) {
			this.tileEntityMachine = tileEntityMachine;
			this.buttonList.add(new GuiButtonIcon(0, 22, 20, tileEntityMachine.isRedstoneActivated() ? 0 : 1, 1));

			maxHeight = 28 + this.buttonList.size() * 20;
			overlayColor = 0x00baa1;
			iconOffsetX = 2;
			iconOffsetY = 0;
		}

		@Override
		public void draw(int x, int y) {			
			super.draw(x, y);
			
			if (!isFullyOpened()) {
				return;
			}	
			fontRenderer.drawStringWithShadow(Localization.get("gui.config.title"), x + 22, y + 8, headerColour);
			
			for (GuiButtonIcon guiButtonIcon : this.buttonList) {
				fontRenderer.drawString(Localization.get("gui.config."+guiButtonIcon.id), x + 22 + guiButtonIcon.xPosition_ + 2, y + guiButtonIcon.yPosition_ + 6, textColour);
			}
		}

		@Override
		public String getTooltip() {
			return Localization.get("gui.config.title");
		}
	}
	
	protected static ResourceLocation TEXTURE;
	protected static final ResourceLocation BLOCK_TEXTURE = TextureMap.locationBlocksTexture;
	
	protected T tileEntity;
	
	public GuiMachine(InventoryPlayer inventoryPlayer, T tileEntity, ContainerMachine<T> container, ResourceLocation texture, String info) {
		super(container);
		this.tileEntity = tileEntity;
		this.ledgerManager.add(new EnergyLedger(tileEntity));
		this.ledgerManager.add(new InfoLedger(info));
		this.ledgerManager.add(new ConfigLedger(tileEntity));
		TEXTURE = texture;
	}

	@Override
    protected void actionPerformed(GuiButton guiButton) {
		if (guiButton.id == 0 && guiButton instanceof GuiButtonIcon) {
			if (tileEntity.isRedstoneActivated()) {
				 tileEntity.setRedstoneActivated(false);
				((GuiButtonIcon) guiButton).iconIndexX = 1;
			} else {
				 tileEntity.setRedstoneActivated(true);
				((GuiButtonIcon) guiButton).iconIndexX = 0;
			}
			
            ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
            DataOutputStream dataoutputstream = new DataOutputStream(bytearrayoutputstream);

            try {
                dataoutputstream.writeInt(0);
                dataoutputstream.writeBoolean(this.tileEntity.isRedstoneActivated());
                this.mc.getNetHandler().addToSendQueue(new Packet250CustomPayload("AdvancedBrewing", bytearrayoutputstream.toByteArray()));
            } catch (Exception exception) {
                exception.printStackTrace();
            }
		}
    }
    
	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
		super.drawGuiContainerBackgroundLayer(par1, par2, par3);

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(TEXTURE);

		int x = (this.width - this.xSize) / 2;
		int y = (this.height - this.ySize) / 2;

		this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);

		float storedEnergy = this.tileEntity.getPowerHandler().getEnergyStored();
		if (storedEnergy > 0) {
			float gaugeWidth = ((storedEnergy) / this.tileEntity.getPowerHandler().getMaxEnergyStored()) * 160f;
    		drawTexturedModalRect(x + 8, y + 8, 0, this.ySize, (int) gaugeWidth, 6);
		}
	}
	
	protected void displayFluidGauge(int j, int k, int line, int col, int squaled, FluidStack fluidStack) {
		if (fluidStack == null) {
			return;
		}

		Fluid fluid = fluidStack.getFluid();

		Icon liquidIcon = null;
		if (fluid != null && fluid.getStillIcon() != null) {
			liquidIcon = fluid.getStillIcon();
		}

		mc.renderEngine.bindTexture(BLOCK_TEXTURE);

		PotionDefinition potionDefinition = Utils.getPotionDefinitionByFluid(fluid);
		
		if (potionDefinition != null && !potionDefinition.getName().equals("water")) {
            int color = potionDefinition.getColor();
            float r = (color >> 16 & 255) / 255.0F;
            float g = (color >> 8 & 255) / 255.0F;
            float b = (color & 255) / 255.0F;
            GL11.glColor4f(r, g, b, 1.0F);
		}
		
		if (liquidIcon != null) {
			int start = 0;
			while (true) {
				int x;

				if (squaled > 16) {
					x = 16;
					squaled -= 16;
				}
				else {
					x = squaled;
					squaled = 0;
				}

				drawTexturedModelRectFromIcon(j + col, k + line + 58 - x - start, liquidIcon, 16, 16 - (16 - x));
				start = start + 16;

				if (x == 0 || squaled == 0) {
					break;
				}
			}
		}

		if (potionDefinition != null) {
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		}

		mc.renderEngine.bindTexture(TEXTURE);
		drawTexturedModalRect(j + col, k + line, 176, 0, 16, 60);
	}
}