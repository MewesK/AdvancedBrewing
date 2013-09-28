package advancedbrewing.gui;

import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.Icon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import org.lwjgl.opengl.GL11;

import advancedbrewing.PotionDefinition;
import advancedbrewing.tileentity.TileEntityBrewery;
import advancedbrewing.tileentity.TileEntityMachine;
import advancedbrewing.tileentity.TileEntityPowered;
import advancedbrewing.utils.Localization;
import advancedbrewing.utils.Utils;

public abstract class GuiMachine<T extends TileEntityMachine> extends GuiLedgered {
	
	protected class EnergyLedger extends Ledger {

		TileEntityPowered tileEntityPowered;
		int headerColour = 0xe1c92f;
		int subheaderColour = 0xaaafb8;
		int textColour = 0x000000;

		public EnergyLedger(TileEntityPowered tileEntityPowered) {
			this.tileEntityPowered = tileEntityPowered;
			maxHeight = 94;
			overlayColor = 0xd46c1f;
		}

		@Override
		public void draw(int x, int y) {
			drawBackground(x, y);
			drawIcon(x, y, 0, 0);

			if (!isFullyOpened()) {
				return;
			}
			
			fontRenderer.drawStringWithShadow(Localization.get("gui.energy.text"), x + 22, y + 8, headerColour);

			fontRenderer.drawStringWithShadow(Localization.get("gui.currentInput.text") + ":", x + 22, y + 20, subheaderColour);
			fontRenderer.drawString(String.format("%.1f MJ/t", tileEntityPowered.getCurrentInput()), x + 22, y + 32, textColour);
			fontRenderer.drawStringWithShadow(Localization.get("gui.stored.text") + ":", x + 22, y + 44, subheaderColour);
			fontRenderer.drawString(String.format("%2.1f MJ", tileEntityPowered.getPowerHandler().getEnergyStored()), x + 22, y + 56, textColour);
			fontRenderer.drawStringWithShadow(Localization.get("gui.consumption.text") + ":", x + 22, y + 68, subheaderColour);
			fontRenderer.drawString(String.format("%3.2f MJ/t", tileEntityPowered.getRecentEnergyAverage()), x + 22, y + 80, textColour);

		}

		@Override
		public String getTooltip() {
			return String.format("%3.2f MJ/t", tileEntityPowered.getRecentEnergyAverage());
		}
	}
	
	protected static ResourceLocation TEXTURE;
	protected static final ResourceLocation BLOCK_TEXTURE = TextureMap.locationBlocksTexture;
	
	protected T tileEntity;

	public GuiMachine(InventoryPlayer inventoryPlayer, T tileEntity, ContainerMachine<T> container, ResourceLocation texture) {
		super(container);
		this.tileEntity = tileEntity;
		this.ledgerManager.add(new EnergyLedger(tileEntity));
		TEXTURE = texture;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(TEXTURE);

		int x = (this.width - this.xSize) / 2;
		int y = (this.height - this.ySize) / 2;

		this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize + (tileEntity instanceof TileEntityBrewery ? 22 : 0));

		float storedEnergy = this.tileEntity.getPowerHandler().getEnergyStored();
		if (storedEnergy > 0) {
			float gaugeWidth = ((storedEnergy) / this.tileEntity.getPowerHandler().getMaxEnergyStored()) * 160f;
    		drawTexturedModalRect(x + 8, y + 8, 0, 166, (int) gaugeWidth, 6);
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
		
		if (potionDefinition != null) {
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