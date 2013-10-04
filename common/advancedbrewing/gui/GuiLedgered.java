/** 
 * Copyright (c) MewK, 2013
 * http://advancedbrewing.mewk.net
 * 
 * Advanced Brewing is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://advancedbrewing.mewk.net/MMPL-1.0.txt
 */

package advancedbrewing.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;

/**
 * Stolen from Buildcraft. Find the original here:
 * https://github.com/BuildCraft/BuildCraft
 */
public abstract class GuiLedgered extends GuiContainer {

	protected static final ResourceLocation LEDGER_TEXTURE = new ResourceLocation("advancedbrewing", "textures/gui/ledger.png");
	protected static final ResourceLocation LEDGER_ICONS_TEXTURE = new ResourceLocation("advancedbrewing", "textures/gui/ledger_icons.png");

	protected LedgerManager ledgerManager = new LedgerManager(this);

	@SuppressWarnings("rawtypes")
	private static Class openedLedger;

	protected class LedgerManager {

		private GuiLedgered gui;
		protected ArrayList<Ledger> ledgers = new ArrayList<Ledger>();

		public LedgerManager(GuiLedgered gui) {
			this.gui = gui;
		}

		public void add(Ledger ledger) {
			this.ledgers.add(ledger);
			if (GuiLedgered.getOpenedLedger() != null && ledger.getClass().equals(GuiLedgered.getOpenedLedger())) {
				ledger.setFullyOpen();
			}
		}

		/**
		 * Inserts a ledger into the next-to-last position.
		 * 
		 * @param ledger
		 */
		public void insert(Ledger ledger) {
			this.ledgers.add(this.ledgers.size() - 1, ledger);
		}

		protected Ledger getAtPosition(int mX, int mY) {
			int xShift = ((this.gui.width - this.gui.xSize) / 2) + this.gui.xSize;
			int yShift = ((this.gui.height - this.gui.ySize) / 2) + 8;

			for (int i = 0; i < this.ledgers.size(); i++) {
				Ledger ledger = this.ledgers.get(i);
				if (!ledger.isVisible()) {
					continue;
				}

				ledger.currentShiftX = xShift;
				ledger.currentShiftY = yShift;
				if (ledger.intersectsWith(mX, mY, xShift, yShift)) {
					return ledger;
				}

				yShift += ledger.getHeight();
			}

			return null;
		}

		protected void drawLedgers(int mouseX, int mouseY) {
			int xPos = 8;
			for (Ledger ledger : this.ledgers) {

				ledger.update();
				if (!ledger.isVisible()) {
					continue;
				}

				for (GuiButtonIcon guiButton : ledger.getButtonList()) {
					guiButton.xPosition = GuiLedgered.this.guiLeft + GuiLedgered.this.xSize + guiButton.xPosition_;
					guiButton.yPosition = GuiLedgered.this.guiTop + xPos + guiButton.yPosition_;
				}

				ledger.draw(GuiLedgered.this.guiLeft + GuiLedgered.this.xSize, GuiLedgered.this.guiTop + xPos);
				xPos += ledger.getHeight();
			}
		}

		protected void drawLedgerTooltips(int mouseX, int mouseY) {
			Ledger ledger = this.getAtPosition(mouseX, mouseY);
			if (ledger != null && !ledger.isOpen()) {
				int startX = mouseX - ((this.gui.width - this.gui.xSize) / 2) + 12;
				int startY = mouseY - ((this.gui.height - this.gui.ySize) / 2) - 12;

				String tooltip = ledger.getTooltip();
				int textWidth = GuiLedgered.this.fontRenderer.getStringWidth(tooltip);
				GuiLedgered.this.drawGradientRect(startX - 3, startY - 3, startX + textWidth + 3, startY + 8 + 3, 0xc0000000, 0xc0000000);
				GuiLedgered.this.fontRenderer.drawStringWithShadow(tooltip, startX, startY, -1);
			}
		}

		public void handleMouseClicked(int x, int y, int mouseButton) {
			if (mouseButton == 0) {

				Ledger ledger = this.getAtPosition(x, y);

				// Default action only if the mouse click was not handled by the
				// ledger itself.
				if (ledger != null && !ledger.handleMouseClicked(x, y, mouseButton)) {
					boolean cancelEvent = false;
					for (GuiButton guiButton : ledger.getButtonList()) {
						if (guiButton.mousePressed(FMLClientHandler.instance().getClient(), x, y)) {
							cancelEvent = true;
							break;
						}
					}

					if (!cancelEvent) {
						for (Ledger other : this.ledgers) {
							if (other != ledger && other.isOpen()) {
								other.toggleOpen();
							}
						}
						ledger.toggleOpen();
					}
				}
			}
		}
	}

	/**
	 * Side ledger for guis
	 */
	protected abstract class Ledger {
		private boolean open;
		protected int overlayColor = 0xffffff;
		protected int headerColour = 0xe1c92f;
		protected int subheaderColour = 0xaaafb8;
		protected int textColour = 0x000000;
		public int currentShiftX = 0;
		public int currentShiftY = 0;
		protected int limitWidth = 128;
		protected int maxWidth = 124;
		protected int minWidth = 24;
		protected int currentWidth = this.minWidth;
		protected int maxHeight = 24;
		protected int minHeight = 24;
		protected int currentHeight = this.minHeight;

		protected int iconOffsetX = 0;
		protected int iconOffsetY = 0;

		protected List<GuiButtonIcon> buttonList = new ArrayList<GuiButtonIcon>();

		public void update() {
			// Width
			if (this.open && this.currentWidth < this.maxWidth) {
				this.currentWidth += 4;
			}
			else if (!this.open && this.currentWidth > this.minWidth) {
				this.currentWidth -= 4;
			}

			// Height
			if (this.open && this.currentHeight < this.maxHeight) {
				this.currentHeight += 4;
			}
			else if (!this.open && this.currentHeight > this.minHeight) {
				this.currentHeight -= 4;
			}
		}

		public int getHeight() {
			return this.currentHeight;
		}

		public void draw(int x, int y) {
			this.drawBackground(x, y);
			this.drawIcon(x, y, this.iconOffsetX, this.iconOffsetY);

			if (!this.isFullyOpened()) {
				for (GuiButton guiButton : this.buttonList) {
					guiButton.drawButton = false;
				}
				return;
			}

			for (GuiButton guiButton : this.buttonList) {
				guiButton.drawButton = true;
			}
		}

		public abstract String getTooltip();

		public boolean handleMouseClicked(int x, int y, int mouseButton) {
			return false;
		}

		public boolean intersectsWith(int mouseX, int mouseY, int shiftX, int shiftY) {

			if (mouseX >= shiftX && mouseX <= shiftX + this.currentWidth && mouseY >= shiftY && mouseY <= shiftY + this.getHeight()) {
				return true;
			}

			return false;
		}

		public void setFullyOpen() {
			this.open = true;
			this.currentWidth = this.maxWidth;
			this.currentHeight = this.maxHeight;
		}

		public void toggleOpen() {
			if (this.open) {
				this.open = false;
				GuiLedgered.setOpenedLedger(null);
			}
			else {
				this.open = true;
				GuiLedgered.setOpenedLedger(this.getClass());
			}
		}

		public boolean isVisible() {
			return true;
		}

		public boolean isOpen() {
			return this.open;
		}

		protected boolean isFullyOpened() {
			return this.currentWidth >= this.maxWidth;
		}

		protected void drawBackground(int x, int y) {
			float colorR = (this.overlayColor >> 16 & 255) / 255.0F;
			float colorG = (this.overlayColor >> 8 & 255) / 255.0F;
			float colorB = (this.overlayColor & 255) / 255.0F;

			GL11.glColor4f(colorR, colorG, colorB, 1.0F);

			GuiLedgered.this.mc.renderEngine.bindTexture(GuiLedgered.LEDGER_TEXTURE);
			GuiLedgered.this.drawTexturedModalRect(x, y, 0, 256 - this.currentHeight, 4, this.currentHeight);
			GuiLedgered.this.drawTexturedModalRect(x + 4, y, 256 - this.currentWidth + 4, 0, this.currentWidth - 4, 4);
			GuiLedgered.this.drawTexturedModalRect(x, y, 0, 0, 4, 4); // Add in
																	  // top
																	  // left
																	  // corner
			// again
			GuiLedgered.this.drawTexturedModalRect(x + 4, y + 4, 256 - this.currentWidth + 4, 256 - this.currentHeight + 4, this.currentWidth - 4, this.currentHeight - 4);

			GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0F);
		}

		protected void drawIcon(int x, int y, int xIndex, int yIndex) {
			GuiLedgered.this.mc.renderEngine.bindTexture(GuiLedgered.LEDGER_ICONS_TEXTURE);
			GuiLedgered.this.drawTexturedModalRect(x + 3, y + 4, xIndex * 16, yIndex * 16, 16, 16);
		}

		public List<GuiButtonIcon> getButtonList() {
			return this.buttonList;
		}
	}

	public GuiLedgered(Container par1Container) {
		super(par1Container);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
		this.ledgerManager.drawLedgers(par2, par3);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		this.ledgerManager.drawLedgerTooltips(par1, par2);
	}

	@Override
	protected void mouseClicked(int par1, int par2, int mouseButton) {
		super.mouseClicked(par1, par2, mouseButton);
		this.ledgerManager.handleMouseClicked(par1, par2, mouseButton);
	}

	@SuppressWarnings("rawtypes")
	public static void setOpenedLedger(Class ledgerClass) {
		GuiLedgered.openedLedger = ledgerClass;
	}

	@SuppressWarnings("rawtypes")
	public static Class getOpenedLedger() {
		return GuiLedgered.openedLedger;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void initGui() {
		super.initGui();
		for (Ledger ledger : this.ledgerManager.ledgers) {
			this.buttonList.addAll(ledger.getButtonList());
		}
	}
}